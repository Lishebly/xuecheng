package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.sun.el.stream.Stream;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.mapper.MediaProcessHistoryMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.model.po.MediaProcessHistory;
import com.xuecheng.media.service.MediaFileService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mr.M
 * @version 1.0
 * @description 文件服务实现类
 * @date 2022/9/10 8:58
 */
@Service
@Slf4j
public class MediaFileServiceImpl implements MediaFileService {

    @Autowired
    private MediaFileService currentProxy;
    @Autowired
    private MediaProcessMapper mediaProcessMapper;
    @Autowired
    MediaFilesMapper mediaFilesMapper;
    @Autowired
    private MinioClient minioClient;
    @Autowired
    private MediaProcessHistoryMapper mediaProcessHistoryMapper;
    //普通文件桶
    @Value("${minio.bucket.files}")
    private String bucketFiles;
    //视频文件桶
    @Value("${minio.bucket.videofiles}")
    private String bucketVideos;

    @Override
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
        return mediaListResult;

    }

    /**
     * 上传文件
     *
     * @param companyId          公司id
     * @param uploadFileParamDto 文件上传参数
     * @param localFilePath      文件路径
     * @return 返回结果
     */
    @Override
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamDto uploadFileParamDto, String localFilePath) {
        //todo 判断公司
        File file = new File(localFilePath);
        if (!file.exists()) {
            XueChengPlusException.cast("文件不存在");
        }
        //准备数据
        String filename = uploadFileParamDto.getFilename();
        //依据年月日加上文件的md5值加上扩展名生成文件名
        String md5 = getMD5(file);
        String extention = getExtention(filename);
        String objname = getMinioPath(md5, extention);
        String mimeType = getMimeType(filename);
        //上传文件到 minio
        boolean flag = uploadToMinio(localFilePath, bucketFiles, mimeType, objname);
        if (!flag) {
            XueChengPlusException.cast("上传文件失败");
        }
        //保存文件到数据库
        uploadFileParamDto.setFileSize(file.length());
        MediaFiles mediaFiles = currentProxy.addMediaFilesToDB(companyId, md5, uploadFileParamDto, bucketFiles, objname);
        UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
        BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
        return uploadFileResultDto;
    }

    public String getMinioPath(String md5, String extention){
        String dateName = getObjName();
        return dateName + md5 + extention;
    }

    @Override
    public boolean deleteFileInMinio(String bucket, String filePath) {
        RemoveObjectArgs args = RemoveObjectArgs.builder().bucket(bucket).object(filePath).build();
        try {
            minioClient.removeObject(args);
            return true;
        } catch (Exception e) {
            log.error("从minio删除文件失败,{}", e.getMessage());
            return false;
        }
    }

    @Override
    public MediaFiles getFileById(String mediaId) {
        return mediaFilesMapper.selectById(mediaId);
    }

    @Transactional
    public MediaFiles addMediaFilesToDB(Long companyId, String md5, UploadFileParamDto uploadFileParamDto, String bucketFiles, String objname) {
        MediaFiles mediaFiles = new MediaFiles();
        BeanUtils.copyProperties(uploadFileParamDto, mediaFiles);
        mediaFiles.setId(md5);
        mediaFiles.setCompanyId(companyId);
        mediaFiles.setFileId(md5);
        mediaFiles.setBucket(bucketFiles);
        mediaFiles.setFilePath(objname);
        mediaFiles.setUrl("/" + bucketFiles + "/" + objname);
        mediaFiles.setCreateDate(LocalDateTime.now());
        mediaFiles.setAuditStatus("002003");
        mediaFiles.setStatus("1");
        MediaFiles mediaFiles1 = mediaFilesMapper.selectById(md5);
        if(mediaFiles1 != null){
            log.debug("文件已存在,{}", mediaFiles.toString());
            return mediaFiles1;
        }
        int insert = mediaFilesMapper.insert(mediaFiles);
        if (insert < 0) {
            //打印日志,信息全一些
            log.error("上传文件到数据库失败,{}", mediaFiles.toString());
            XueChengPlusException.cast("上传文件到数据库失败");
        }
        log.debug("上传文件到数据库成功,{}", mediaFiles.toString());
        //插入到待处理任务表
        addToTaskWaitTable(mediaFiles);
        return mediaFiles;
    }

    private void addToTaskWaitTable(MediaFiles mediaFiles) {
        //判断文件类型是否为需要转换的文件
        String filename = mediaFiles.getFilename();
        String extention = filename.substring(filename.lastIndexOf("."));
        String mimeType = getMimeType(filename);
        if (!mimeType.equals("video/x-msvideo")) {
            return;
        }
        MediaProcess mediaProcess = new MediaProcess();
        BeanUtils.copyProperties(mediaFiles, mediaProcess);
        mediaProcess.setUrl(null);
        mediaProcess.setCreateDate(LocalDateTime.now());
        mediaProcess.setStatus("1");
        int insert1 = mediaProcessMapper.insert(mediaProcess);
        if (insert1 < 0) {
            //打印日志,信息全一些
            log.error("上传文件到待处理任务表失败,{}", mediaProcess.toString());
            XueChengPlusException.cast("上传文件到待处理任务表失败");
        }
    }

    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {
        //查询文件信息
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles == null) {
            return RestResponse.success(false);
        }
        String bucket = mediaFiles.getBucket();
        String filePath = mediaFiles.getFilePath();
        //判断文件是否存在
        InputStream object = null;
        try {
            object = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(filePath)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return RestResponse.success(object != null);
    }

    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {
        //获取分块存取的目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //拼接文件名
        String objPath = chunkFileFolderPath + chunkIndex;
        //判断文件是否存在
        InputStream fileInputStream = null;
        try {
            fileInputStream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketVideos)
                    .object(objPath)
                    .build());
        } catch (Exception e) {

        }
        return RestResponse.success(fileInputStream != null);
    }

    @Override
    public RestResponse uploadChunk(String fileMd5, int chunk, String localFilePath, String fileName) {
        //获取分块存取的目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //拼接文件名
        String objPath = chunkFileFolderPath + chunk;
        String mimeType = getMimeType(fileName);
        boolean uploadToMinio = uploadToMinio(localFilePath, bucketVideos, mimeType, objPath);
        if (!uploadToMinio) {
            return RestResponse.validfail(false, "上传文件失败");
        }
        return RestResponse.success(true);
    }

    @Override
    public RestResponse mergechunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamDto uploadFileParamsDto) {
        //获取文件分块目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //分块 list
        List<ComposeSource> composeSources = new ArrayList<>();
        for (int i = 0; i < chunkTotal; i++) {
            ComposeSource build = ComposeSource.builder()
                    .bucket(bucketVideos)
                    .object(chunkFileFolderPath.concat(Integer.toString(i)))
                    .build();
            composeSources.add(build);
        }
        String fileName = uploadFileParamsDto.getFilename();
        //文件扩展名
        String extName = fileName.substring(fileName.lastIndexOf("."));
        String mergeName = getfileNameByMd5(fileMd5, extName);
        //合并文件
        try {
            ObjectWriteResponse objectWriteResponse = minioClient.composeObject(ComposeObjectArgs.builder()
                    .bucket(bucketVideos)
                    .object(mergeName)
                    .sources(composeSources)
                    .build());
            log.debug("合并文件成功,{}", objectWriteResponse.toString());
        } catch (Exception e) {
            log.error("合并文件失败,{}", e.getMessage());
            return RestResponse.validfail(false, "合并文件失败。");
        }

        //比较文件md5
        File minioFile = downloadFileFromMinIO(bucketVideos, mergeName);
        if (minioFile == null) {
            log.debug("下载合并后文件失败,mergeFilePath:{}", mergeName);
            return RestResponse.validfail(false, "下载合并后文件失败。");
        }
        try (FileInputStream fileInputStream = new FileInputStream(minioFile)) {
            String md5Hex = DigestUtils.md5Hex(fileInputStream);
            if (!fileMd5.equals(md5Hex)) {
                log.debug("文件md5不一致,md5Hex:{}", md5Hex);
                return RestResponse.validfail(false, "文件md5不一致。");
            }
            uploadFileParamsDto.setFileSize(minioFile.length());
        } catch (Exception e) {
            log.error("比较文件md5失败,{}", e.getMessage());
            return RestResponse.validfail(false, "比较文件md5失败。");
        } finally {
            minioFile.delete();
        }

        //文件信息入库
        currentProxy.addMediaFilesToDB(companyId, fileMd5, uploadFileParamsDto, bucketVideos, mergeName);

        //清理分块文件
        deleteChunks(chunkFileFolderPath, chunkTotal);
        return RestResponse.success(true);
    }

    @Override
    public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count) {
        return mediaProcessMapper.selectListByShardIndex(shardTotal, shardIndex, count);
    }

    @Override
    public boolean startTask(long id) {
        int result = mediaProcessMapper.startTask(id);
        return result > 0;
    }

    /**
     * 保存任务执行状态
     *
     * @param taskId
     * @param status
     * @param fileId
     * @param url
     * @param errorMsg
     */
    @Override
    @Transactional
    public void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg) {
        //查询任务,不存在,直接返回
        MediaProcess mediaProcess = mediaProcessMapper.selectById(taskId);
        if (mediaProcess == null) {
            return;
        }
        //插入成功时
        if (status.equals("2")) {
            //更新媒体文件的 url
            MediaFiles mediaFiles = mediaFilesMapper.selectById(fileId);
            if (mediaFiles != null) {
                mediaFiles.setUrl(url);
                mediaFiles.setChangeDate(LocalDateTime.now());
                mediaFilesMapper.updateById(mediaFiles);
            }
            MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
            BeanUtils.copyProperties(mediaProcess, mediaProcessHistory);
            mediaProcessHistory.setStatus(status);
            mediaProcessHistory.setUrl(url);
            mediaProcessHistory.setFinishDate(LocalDateTime.now());
            mediaProcessHistoryMapper.insert(mediaProcessHistory);
            //删除待处理文件
            mediaProcessMapper.deleteById(taskId);
        } else if (status.equals("3")) {
            mediaProcess.setFailCount(mediaProcess.getFailCount() + 1);
            mediaProcess.setErrormsg(errorMsg);
            mediaProcess.setStatus(status);
            mediaProcessMapper.updateById(mediaProcess);
            log.debug("任务执行失败,任务信息为:{}", mediaProcess);
        }

    }

    private void deleteChunks(String chunkFileFolderPath, int chunkTotal) {
        try {
            List<DeleteObject> deleteObjects = new ArrayList<>();
            for (int i = 0; i < chunkTotal; i++) {
                DeleteObject deleteObject = new DeleteObject(chunkFileFolderPath.concat(Integer.toString(i)));
                deleteObjects.add(deleteObject);
            }
            RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder().bucket(bucketVideos).objects(deleteObjects).build();
            Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
            results.forEach(result -> {
                DeleteError deleteError = null;
                try {
                    deleteError = result.get();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("清除分块文件失败,objectname:{}", deleteError.objectName(), e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            log.error("清除分块文件失败,chunkFileFolderPath:{}", chunkFileFolderPath);
        }
    }

    public File downloadFileFromMinIO(String bucketVideos, String mergeName) {
        //临时文件
        File minioFile = null;
        FileOutputStream outputStream = null;
        try {
            InputStream object = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketVideos)
                    .object(mergeName).build());
            //创建临时文件
            minioFile = File.createTempFile("minio", "tmp");
            outputStream = new FileOutputStream(minioFile);
            IOUtils.copy(object, outputStream);
            return minioFile;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private String getfileNameByMd5(String fileMd5, String extName) {
        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" + fileMd5 + extName;
    }

    private String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" + "chunk" + "/";
    }

    public boolean uploadToMinio(String localFilePath, String bucketName, String mimeType, String minioPath) {
        try {
            UploadObjectArgs args = UploadObjectArgs.builder()
                    .bucket(bucketName)
                    .object(minioPath)
                    .filename(localFilePath)
                    .contentType(mimeType)
                    .build();
            minioClient.uploadObject(args);
            log.debug("桶{},文件名{}上传成功", bucketName, minioPath);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("桶{},文件名{}上传失败", bucketName, minioPath);
            return false;
        }
    }

    public String getMimeType(String filename) {
        //根据扩展名取出mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(filename);
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;//通用mimeType，字节流
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }

    private String getExtention(String filename) {
        String fileName = StringUtils.substringAfterLast(filename, ".");
        return "." + fileName;
    }

    private String getMD5(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            String md5 = DigestUtils.md5Hex(fileInputStream);
            return md5;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getObjName() {
        //根据年月日生成目录格式为2024/1/1/
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd/");
        return simpleDateFormat.format(new Date());
    }
}

