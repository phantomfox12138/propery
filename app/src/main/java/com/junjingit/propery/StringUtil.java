/**
 * 
 */
package com.junjingit.propery;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * 
 * @author 牛凡
 */
public class StringUtil
{
    
    /**
     * 系统时间戳格式
     */
    public static final SimpleDateFormat TIMESTAMP_DF = new SimpleDateFormat(
            "yyyyMMddHHmmss");
    
    private static final SimpleDateFormat sdf = new SimpleDateFormat(
            "yyyy-MM-dd");
    
    public static String formatDate(Date date) throws ParseException
    {
        return sdf.format(date);
    }
    
    public static Date parse(String strDate) throws ParseException
    {
        
        return sdf.parse(strDate);
    }
    
    /**
     * 去除字符串空格，回车
     */
    public static String replaceBlank(String str)
    {
        String dest = "";
        if (str != null)
        {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
    
    /**
     * 判断是否为null或空值
     * 
     * @param str
     *            String
     * @return true or false
     */
    public static boolean isNullOrEmpty(String str)
    {
        return str == null || str.trim().length() == 0;
    }
    
    /**
     * 判断字符串中是否包含中文 <BR>
     * 
     * @param str
     *            检索的字符串
     * @return 是否包含中文
     */
    public static boolean hasChinese(String str)
    {
        boolean hasChinese = false;
        if (str != null)
        {
            for (char c : str.toCharArray())
            {
                if (isChinese(c))
                {
                    hasChinese = true;
                    break;
                }
            }
        }
        return hasChinese;
    }
    
    /**
     * 
     * 判断参数c是否为中文<BR>
     * 
     * @param c
     *            char
     * @return 是中文字符返回true，反之false
     */
    public static boolean isChinese(char c)
    {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
        
    }
    
    /**
     * 
     * 根据默认的格式获得当前时间字符串<BR>
     * 
     * @return 当前时间
     */
    public static String getCurrentDateString()
    {
        return TIMESTAMP_DF.format(new Date());
    }
    
    /**
     * 验证字符串是否符合email格式
     * 
     * @param email
     *            需要验证的字符串
     * @return 验证其是否符合email格式，符合则返回true,不符合则返回false
     */
    public static boolean isEmail(String email)
    {
        
        // 通过正则表达式验证email是否合法
        return email != null
                && email.matches("(\\w[\\w\\.\\-]*)@\\w[\\w\\-]*[\\.(com|cn|org|edu|hk)]+[a-z]$");
    }
    
    /**
     * 
     * 去掉url中多余的斜杠
     * 
     * @param url
     *            字符串
     * @return 去掉多余斜杠的字符串
     */
    public static String fixUrl(String url)
    {
        if (null == url)
        {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer(url);
        for (int i = stringBuffer.indexOf("//", stringBuffer.indexOf("//") + 2); i != -1; i = stringBuffer.indexOf("//",
                i + 1))
        {
            stringBuffer.deleteCharAt(i);
        }
        return stringBuffer.toString();
    }
    
    /**
     * 判断str1和str2是否相同
     * 
     * @param str1
     *            str1
     * @param str2
     *            str2
     * @return true or false
     */
    public static boolean equals(String str1, String str2)
    {
        return str1 == str2 || str1 != null && str1.equals(str2);
    }
    
    /**
     * 判断str1和str2是否相同(不区分大小写)
     * 
     * @param str1
     *            str1
     * @param str2
     *            str2
     * @return true or false
     */
    public static boolean equalsIgnoreCase(String str1, String str2)
    {
        return str1 != null && str1.equalsIgnoreCase(str2);
    }
}
