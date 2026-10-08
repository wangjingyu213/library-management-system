package com.example.library.mapper;

import com.example.library.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

public interface UserMapper {
    Optional<User> findById(@Param("id") Long id);
    Optional<User> findByUsername(@Param("username") String username);
    List<User> findAll();
    List<String> findRolesByUserId(@Param("userId") Long userId);
    List<String> findPermissionsByUserId(@Param("userId") Long userId);
    Long findRoleIdByCode(@Param("code") String code);
    int insert(User user);
    int update(User user);
    int deleteRoles(@Param("userId") Long userId);
    int insertRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
    int deleteById(@Param("id") Long id);
    int countByUsername(@Param("username") String username, @Param("excludeId") Long excludeId);
}
