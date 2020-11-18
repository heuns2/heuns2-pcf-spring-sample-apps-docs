package com.mzc.boot;

import java.util.Map;

public interface CacheImpl {
	
	Map<String, Object> putCache(String key, Map<String, Object> value);
}
