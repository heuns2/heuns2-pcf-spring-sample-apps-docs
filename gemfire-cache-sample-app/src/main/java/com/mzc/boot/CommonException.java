package com.mzc.boot;

import java.util.HashMap;
import java.util.List;

import org.yaml.snakeyaml.constructor.DuplicateKeyException;

public class CommonException extends RuntimeException {
	
	private static final HashMap<String, Integer> internalServerErrorCodeMap;
    private static final Integer BIZ_ERR_RETURN_CODE = 701;
    private static final Integer SYS_ERR_RETURN_CODE = 600;
    public static final Integer APP_ERR_NON_RETRY_RETURN_CODE = 702; 
    private static final Integer APP_ERR_RETRY_RETURN_CODE = 601;
    private static final Integer SYS_ERR_RETRY_RETURN_CODE = 602;
    private static final Integer EAI_COMMIT_ERR_RETURN_CODE = 703;
    private List<Throwable> mDetails;
    private Integer code;
    private String message;
    private Object[] list;
    

    private static final HashMap<Class, Integer> internalServerErrorClassMap;

    
    public CommonException(String errorCodeOrMessage) {

        try {
            Integer mappedErrorCode = internalServerErrorCodeMap.get(errorCodeOrMessage);
            if (mappedErrorCode == null || mappedErrorCode == 0) {
                Integer parsedCode = Integer.parseInt(errorCodeOrMessage);
                this.code = parsedCode;
            } else {
                this.code = mappedErrorCode;
            }
            
            System.out.println("==========================CODE VALUE===============================");
            
            
            
        } catch (Exception e) {
            this.code = 500;
            this.message = errorCodeOrMessage;
        }
    }
    
    static {
        internalServerErrorCodeMap = new HashMap<String, Integer>();

        internalServerErrorCodeMap.put("MSG-PM000130", BIZ_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("MSG-PM000136", BIZ_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("MSG-PM000250", BIZ_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("MSG-PM000255", BIZ_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("MSG-PM000288", BIZ_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("MSG-PM000290", BIZ_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("MSG-SD000400", BIZ_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("MSG-CME00510", BIZ_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("MSG-MSGCK133", BIZ_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("BR-0100", BIZ_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("BR-0200", BIZ_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("BR-0300", BIZ_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("BR-0400", BIZ_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("BR-0500", BIZ_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("BR-0600", BIZ_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("BR-0700", BIZ_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("BR-0800", BIZ_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("BR-0900", BIZ_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("BR-1000", BIZ_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("BR-1100", BIZ_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("BR-1200", BIZ_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("BR-1300", BIZ_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("BR-1400", BIZ_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("BR-1500", BIZ_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("BR-1610", BIZ_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("BR-1620", BIZ_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("BR-1630", BIZ_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("BR-1640", BIZ_ERR_RETURN_CODE);
        // 2003-11-24 추가 (TC 미지정)
        internalServerErrorCodeMap.put("MSG-BLFA0061", BIZ_ERR_RETURN_CODE);
        // 2003-11-27 추가 (Attribute value cannot be null)
        internalServerErrorCodeMap.put("JBO-27014", BIZ_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("27014", BIZ_ERR_RETURN_CODE);

        /*
         * System Error
         */

        internalServerErrorCodeMap.put("25014", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("25201", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("25223", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("26030", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("26061", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("26065", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("26066", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("26067", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("26069", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("26092", SYS_ERR_RETURN_CODE); // 2016-04-08 김범재 신규 jbo exception(lockException)
        // 으로 추가
        internalServerErrorCodeMap.put("28100", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("28102", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("30003", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("30008", SYS_ERR_RETURN_CODE);
        // fail to load meta data (BC4J bug)
        internalServerErrorCodeMap.put("MSG-AM025002", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("MSG-MDHL0134", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("MSG-MDHL0193", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("MSG-MDHL0242", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("MSG-MDHL0234", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("MSG-MDHL0293", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("MSG-MDHL0342", SYS_ERR_RETURN_CODE);

        // 2004-07-21 added
        // fail to send TC or File
        internalServerErrorCodeMap.put("MSG-SM000082", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("MSG-SF000094", SYS_ERR_RETURN_CODE);

        // 정해진 시간동안 lock 획득 시도 시 timeout 발생
        // [ JBO-26080, ORA-30006 ]
        // - RetrieveData 에서 lock 시도 : MSG-LOCK0010
        // - SaveData 의 SaveAlter 에서 lock 시도 : MSG-LOCK0015
        // - Custom Activity 에서 lock 시도 : MSG-LOCK0020
        internalServerErrorCodeMap.put("MSG-LOCK0010", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("MSG-LOCK0015", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("MSG-LOCK0020", SYS_ERR_RETURN_CODE);

        // Select 후 Lock 시도 시 다른 TX에서 Data를 삭제한 경우
        internalServerErrorCodeMap.put("25019", SYS_ERR_RETURN_CODE);

        // DB DeadLock 발생
        // [ ORA-00060 ]
        // - SaveData 에서 deadlock 발생 : MSG-LOCK0444
        // - Custom Activity 에서 deadlock 발생 : MSG-LOCK0404
        internalServerErrorCodeMap.put("MSG-LOCK0444", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("MSG-LOCK0404", SYS_ERR_RETURN_CODE);

        // 2004-10-18 추가
        // fail to commit EAI transaction
        // but BC4J Transaction was committed
        internalServerErrorCodeMap.put("MSG-CNTL0601", EAI_COMMIT_ERR_RETURN_CODE);

        // Unique Constraint 와 같이 SQL 레벨의 에러 유형을 Catch하여 Retrun 코드 표준화
        //2020-02-24 Unique Constraint는 Retry 대상 제외 -> 701로 변경
        /*internalServerErrorCodeMap.put("ORA-00001", SYS_ERR_RETURN_CODE);*/
        internalServerErrorCodeMap.put("ORA-00001", BIZ_ERR_RETURN_CODE);

        // Lock wait NOWAIT 지정 중이고 사용중
        internalServerErrorCodeMap.put("ORA-00054", SYS_ERR_RETURN_CODE);

        // Deadlock
        internalServerErrorCodeMap.put("ORA-00060", SYS_ERR_RETURN_CODE);

        // For WAIT update timeout
        internalServerErrorCodeMap.put("ORA-30006", SYS_ERR_RETURN_CODE);

        internalServerErrorCodeMap.put("ORA-08000", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("ORA-08001", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("ORA-08003", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("ORA-08004", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("ORA-08006", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("ORA-08007", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("ORA-82100", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("ORA-12150", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("ORA-12151", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("ORA-12153", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("ORA-12154", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("ORA-12155", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("ORA-12157", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("ORA-12201", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("ORA-12203", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("ORA-12500", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("ORA-12545", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("ORA-12560", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("ORA-12634", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("ORA-12636", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("ORA-12637", SYS_ERR_RETURN_CODE);
        internalServerErrorCodeMap.put("ORA-17002", SYS_ERR_RETURN_CODE);

        internalServerErrorClassMap = new HashMap<>();
        internalServerErrorClassMap.put(DuplicateKeyException.class, SYS_ERR_RETURN_CODE);

    }

    
}
