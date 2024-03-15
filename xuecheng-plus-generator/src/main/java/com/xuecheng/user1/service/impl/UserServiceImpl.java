package com.xuecheng.user1.service.impl;

import com.xuecheng.user1.model.po.User;
import com.xuecheng.user1.mapper.UserMapper;
import com.xuecheng.user1.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * 用户信息 服务实现类
 * </p>
 *
 * @author lishebly
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
