LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
LOCAL_JAVA_LIBRARIES := bouncycastle conscrypt telephony-common ims-common
LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_CERTIFICATE := platform
LOCAL_PACKAGE_NAME := FPSettings

#LOCAL_OVERRIDES_PACKAGES := Settings

LOCAL_STATIC_JAVA_LIBRARIES := \
    android-support-v4 \

# SPRD: remove
#LOCAL_SDK_VERSION := current

#LOCAL_PROGUARD_FLAG_FILES := proguard.flags

include $(BUILD_PACKAGE)

# Use the folloing include to make our test apk.
include $(call all-makefiles-under,$(LOCAL_PATH))
