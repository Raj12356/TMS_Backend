package com.tms;

import com.tms.util.PasswordUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    void testVerifyExistingDbPassword() {
        String existingHash = "scrypt$b3c296fb396a9aea3c408f98c32fbfef$c04a7a1d81a12a617e08f1eebec3ded5a60b51889d65db788097246fbd6f4af672178f470c06db8cd5dbf6a5258029bcf00e7c0a66b496367a5e0c7578bfc9cc";
        assertTrue(PasswordUtil.verifyPassword("admin123", existingHash));
        assertFalse(PasswordUtil.verifyPassword("wrongpass", existingHash));
    }

    @Test
    void testHashAndVerifyNewPassword() {
        String hash = PasswordUtil.hashPassword("secretPass!2026");
        assertTrue(PasswordUtil.isHashed(hash));
        assertTrue(PasswordUtil.verifyPassword("secretPass!2026", hash));
        assertFalse(PasswordUtil.verifyPassword("otherPass", hash));
    }
}

