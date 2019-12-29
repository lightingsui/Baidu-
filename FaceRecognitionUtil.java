package com.repository.utils;

import com.baidu.aip.face.AipFace;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.repository.vo.FaceMessage;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 百度AI人脸识别
 *
 * <p>百度AI人脸识别
 *
 * @author ：lightingsui
 * @since ：Created in 2019/11/30 18:13
 */
public class FaceRecognitionUtil {
    private static AipFace client = AipFaceUtil.getAipFace();

    private static final Boolean DELETE_FACE_FAILED = false;
    private static final String NO_SUCH_AS_USER_FACE = "null";
    private static final String INSERT_USER_FACE_NOT_ERROR = "0";

    /**
     * 比对人脸信息
     *
     * <p>根据用户传入的图片Base64编码，去人脸库中
     * 查找，可能会查找到多个，最后只选出评分最高的
     *
     * @param Base64 人脸图像Base64编码
     * @throws JsonProcessingException json解析异常
     * @return 如果查到了，则返回人脸所对应的用户信息 {@link FaceMessage}
     *         查找不到则返回 {@code null}
     */
    public static FaceMessage searchFace(String Base64) {
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("max_face_num", "3");
        options.put("match_threshold", "80");
        options.put("quality_control", "LOW");
        options.put("liveness_control", "NONE");
        options.put("max_user_num", "3");

        String image = Base64;
        String imageType = "BASE64";
        String groupIdList = "repo_group";

        // 人脸搜索
        JSONObject res = client.search(image, imageType, groupIdList, options);

        // 将结果转化为对象
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonNode = null;
        FaceMessage faceMessage = null;
        try {
            // 只取用户人脸评分最高的进行转换进行转换
            String resultNode = objectMapper.readTree(String.valueOf(res)).get("result").toString();

            if(StringUtils.equals(resultNode, NO_SUCH_AS_USER_FACE)){
                // 没有匹配的人脸返回 null
                return null;
            }

            jsonNode = objectMapper.readTree(resultNode).get("user_list").get(0).toString();
            faceMessage = objectMapper.readValue(jsonNode, FaceMessage.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return faceMessage;
    }

    /**
     * 删除百度云人脸库用户信息
     *
     * <p>传入百度云中的用户标识，删掉关于此用户的信息，
     * 包括人脸信息。
     *
     * @param userIdentified 用户标识信息
     * @throws JsonProcessingException json解析错误
     * @return 如果删除成功，将返回 {@code true}
     *         如果删除失败，将返回 {@code false}
     */
    public static boolean deleteFace(String userIdentified){
        HashMap<String, String> options = new HashMap<String, String>();

        String groupId = "repo_group";
        String userId = userIdentified;

        // 删除用户
        JSONObject res = client.deleteUser(groupId, userId, options);

        ObjectMapper objectMapper = new ObjectMapper();
        String errorMsg = null;
        try {
            // 获得错误信息
            errorMsg = objectMapper.readTree(res.toString()).get("error_code").toString();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        System.out.println(res);

        if(errorMsg.equals(INSERT_USER_FACE_NOT_ERROR)){
            // 没有错误
            return true;
        }else {
            // 有错误
            return false;
        }
    }

    /**
     * 向人脸库中添加人脸
     *
     * <p>调用百度API向人脸库中添加人脸信息，
     * 如果在用户人脸库中已经存在人脸，则更新
     * 人脸
     *
     * @param base64Image 人脸图片的BASE64编码
     * @throws JsonProcessingException 解析json异常
     * @return 如果插入成功，则返回 {@code false}
     *         如果从插入失败，则返回 {@code true}
     */
    public static boolean uploadPace(String base64Image, String userIdentified) {
        // 调整字符串
        base64Image = base64Image.substring(base64Image.indexOf(",") + 1);

        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("user_info", "user's info");
        options.put("quality_control", "NORMAL");
        options.put("liveness_control", "NONE");
        options.put("action_type", "REPLACE");

        String image = base64Image;
        String imageType = "BASE64";
        String groupId = "repo_group";
        String userId = userIdentified;

        // 人脸注册
        JSONObject res = client.addUser(image, imageType, groupId, userId, options);

        ObjectMapper objectMapper = new ObjectMapper();
        String errorMsg = null;
        try {
            // 获得错误信息
            errorMsg = objectMapper.readTree(res.toString()).get("error_code").toString();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        System.out.println(res);

        if(INSERT_USER_FACE_NOT_ERROR.equals(errorMsg)){
            // 没有错误
            return false;
        }else {
            // 有错误
            return true;
        }
    }

    /**
     * 修改用户标识信息
     *
     * <p>修改用户标识 {@code ID}，标识 {@code ID}
     * 相当用户数据库中的主键，也就是百度人脸库中用户的主键
     *
     * @param base64Image 人脸base64编码
     * @param userIdentified 用户新的标识 {@code ID}
     * @param oldIdentified 用户原有的标识 {@code ID}
     * @return 如果修改成功，将返回 {@code true}
     *         如果修改失败，将返回 {@code false}
     */
    public static boolean changeUserIdentified(String base64Image, String userIdentified, String oldIdentified){
        boolean deleteResult = deleteFace(oldIdentified);

        if(deleteResult == DELETE_FACE_FAILED){
            return false;
        }

        boolean updateResult = uploadPace(base64Image, userIdentified);

        return !updateResult;
    }
}
