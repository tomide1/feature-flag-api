package com.iw.featureflag.security.role;

public interface RoleService {
    void addRole(String uid, String role) throws Exception;
    void removeRole(String uid, String role);
}
