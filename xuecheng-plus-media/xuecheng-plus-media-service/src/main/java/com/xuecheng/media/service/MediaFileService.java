package com.xuecheng.media.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.model.po.MediaProcess;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.File;
import java.util.List;

/**
 * @description 媒资文件管理业务类
 * @author Mr.M
 * @date 2022/9/10 8:55
 * @version 1.0
 */
public interface MediaFileService {

 /**
  * @description 媒资文件查询方法
  * @param pageParams 分页参数
  * @param queryMediaParamsDto 查询条件
  * @return com.xuecheng.base.model.PageResult<com.xuecheng.media.model.po.MediaFiles>
  * @author Mr.M
  * @date 2022/9/10 8:57
 */
 public PageResult<MediaFiles> queryMediaFiels(Long companyId,PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

 /**
  * 上传文件
  * @param companyId 公司id
  * @param uploadFileParamDto 文件参数
  * @param localFilePath 本地文件路径
  * @return com.xuecheng.media.model.dto.UploadFileResultDto
  */
 public UploadFileResultDto uploadFile(Long companyId, UploadFileParamDto uploadFileParamDto , String localFilePath);

 /**
  * 添加文件到数据库
  * @param companyId 公司id
  * @param md5 文件md5
  * @param uploadFileParamDto 文件参数
  * @param bucketFiles 文件存储路径
  * @param objname 文件名称
  * @return com.xuecheng.media.model.po.MediaFiles
  */
 public MediaFiles addMediaFilesToDB(Long companyId, String md5, UploadFileParamDto uploadFileParamDto, String bucketFiles, String objname);


 /**
  * 文件上传前检查文件
  * @param fileMd5 文件md5
  * @return
  */
 public RestResponse<Boolean> checkFile(String fileMd5);


 /**
  * 文件上传前检查分块是否上传
  * @param fileMd5
  * @param chunkIndex
  * @return
  */
 public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);


 /**
  * 分块文件上传
  * @param fileMd5
  * @param chunk
  * @return
  */
 public RestResponse uploadChunk(String fileMd5,int chunk,String localFilePath,String fileName);

 /**
  * 合并文件
  * @param companyId 公司id
  * @param fileMd5 文件md5
  * @param chunkTotal 分块总数
  * @param uploadFileParamsDto 文件参数
  * @return
  */
 public RestResponse mergechunks(Long companyId,String fileMd5,int chunkTotal,UploadFileParamDto uploadFileParamsDto);

 /**
  * 获取待处理的任务列表
  * @param shardIndex
  * @param shardTotal
  * @param count
  * @return
  */
 public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count);


 /**
  *  开启一个任务
  * @param id 任务id
  * @return true开启任务成功，false开启任务失败
  */
 public boolean startTask(long id);

 /**
  * @description 保存任务结果
  * @param taskId  任务id
  * @param status 任务状态
  * @param fileId  文件id
  * @param url url
  * @param errorMsg 错误信息
  * @return void
  * @author Mr.M
  * @date 2022/10/15 11:29
  */
 void saveProcessFinishStatus(Long taskId,String status,String fileId,String url,String errorMsg);


 public File downloadFileFromMinIO(String bucketVideos, String mergeName);

 /**
  * @description 上传文件到minio
  * @param localFilePath 本地文件路径
  * @param bucketName bucket名称
  * @param mimeType 文件类型
  * @param minioPath minio路径
  * @return boolean
  * @author Mr.M
  * @date 2022/10/15 11:29
  */
 public boolean uploadToMinio(String localFilePath, String bucketName, String mimeType, String minioPath);

 /**
  * @description 获取文件类型
  * @param filename 文件名称
  * @return java.lang.String
  * @author Mr.M
  * @date 2022/10/15 11:30
  */
 public String getMimeType(String filename);

 /**
  * @description 获取minio路径
  * @param md5 文件md5
  * @param extention 文件扩展名
  * @return java.lang.String
  * @author Mr.M
  * @date 2022/10/15 11:30
  */
 public String getMinioPath(String md5, String extention);

 /**
  * @description 删除minio文件
  * @param bucket bucket名称
  * @param filePath 文件路径
  * @return void
  * @author Mr.M
  * @date 2022/10/15 11:30
  */
 public boolean deleteFileInMinio(String bucket, String filePath);
}
