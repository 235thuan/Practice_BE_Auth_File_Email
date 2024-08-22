package com.example.authenticationauthorization.enums;

public enum SecurityConstantsENUM {

    // Định nghĩa các URL được phép truy cập
    PERMIT_ALL(new String[]{
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/logout",
            "/api/auth",
            "/api/auth/*",
            "/api/auth/softdel/*",
            "/api/permission",
            "/api/role",
            "/api/auth/verify",
            "/api/files",
            "/api/files/download/*",
            "/api/files/upload/*",
            "/api/files/delete/*",
            "/api/files/infor/*",
            "/api/files/*",
            "/api/files/convert"



    }),
    PERMIT_USER(new String[]{
            "/api/profile"
    }),
    PERMIT_ADMIN(new String[]{
            "/api/auth",
            "/api/auth/*",
            "/api/auth/softdel/*"
    }),
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_USER("ROLE_USER"),

    JWT_EXPIRATION(70000); // Thời gian hết hạn 70 giây (70000 mili giây)




    // Biến lưu trữ URL hoặc thời gian hết hạn
    private final String[] urls;
    private final long expirationTime;
    private final String role;

    SecurityConstantsENUM(String[] urls) {
        this.urls = urls;
        this.expirationTime = -1; // Thay vì khởi tạo thời gian hết hạn, đặt giá trị không hợp lệ
        this.role = null;

    }

    // Constructor cho thời gian hết hạn
    SecurityConstantsENUM(long expirationTime) {
        this.urls = null; // Không sử dụng URL trong trường hợp này
        this.expirationTime = expirationTime;
        this.role = null;

    }

    // Constructor cho các vai trò
    SecurityConstantsENUM(String role) {
        this.urls = null;
        this.expirationTime = -1;
        this.role = role;
    }

    // Phương thức để lấy các URL
    public String[] getUrls() {
        return urls != null ? urls.clone() : null;
    }

    // Phương thức để lấy thời gian hết hạn
    public long getExpirationTime() {
        return expirationTime;
    }

    // Phương thức để lấy vai trò
    public String getRole() {
        return role;
    }

}
