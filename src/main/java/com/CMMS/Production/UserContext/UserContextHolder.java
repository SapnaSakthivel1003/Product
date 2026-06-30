package com.CMMS.Production.UserContext;

public class UserContextHolder {

        private static final ThreadLocal<UserContext> USER_CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

        public static void setContext(UserContext context) {
            USER_CONTEXT_THREAD_LOCAL.set(context);
        }

        public static UserContext getContext() {
            return USER_CONTEXT_THREAD_LOCAL.get();
        }

        public static void clearContext() {
            USER_CONTEXT_THREAD_LOCAL.remove();
        }
    }



