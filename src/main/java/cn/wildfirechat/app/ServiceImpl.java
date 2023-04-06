package cn.wildfirechat.app;

import cn.wildfirechat.common.ErrorCode;
import cn.wildfirechat.pojos.*;
import cn.wildfirechat.sdk.AdminConfig;
import cn.wildfirechat.sdk.MessageAdmin;
import cn.wildfirechat.sdk.UserAdmin;
import cn.wildfirechat.sdk.model.IMResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@org.springframework.stereotype.Service
public class ServiceImpl implements Service {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceImpl.class);

    @Value("${im.admin_url}")
    private String mImUrl;

    @Value("${im.admin_secret}")
    private String mImSecret;

    @Value("${forward.conversation_type}")
    private int mFwdType;

    @Value("${forward.conversation_target}")
    private String mFwdTarget;

    @Value("${im.notification_text}")
    private String mNotificationText;

    @Value("${im.notification_text2}")
    private String mNotificationText2;

    private ExecutorService cacheExecutor;

    @PostConstruct
    private void init() {
        AdminConfig.initAdmin(mImUrl, mImSecret);
        cacheExecutor = Executors.newCachedThreadPool();
    }

    @Override
    public void onReceiveMessage(OutputMessageData outputMessageData) {
        if (outputMessageData.getSender().equals("FireRobot") || (outputMessageData.getConv().getType() == 1 && outputMessageData.getConv().getTarget().equals("FireRobot"))) {
            LOG.info("机器人消息忽略");
            return;
        }

        if (outputMessageData.getSender().equals("admin") || (outputMessageData.getConv().getType() == 1 && outputMessageData.getConv().getTarget().equals("admin"))) {
            LOG.info("Admin消息忽略");
            return;
        }

        cacheExecutor.submit(() -> {
            try {
                IMResult<InputOutputUserInfo> outputUserInfoIMResult = UserAdmin.getUserByUserId(outputMessageData.getSender());
                if (outputUserInfoIMResult != null && outputUserInfoIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                    String displayName = outputUserInfoIMResult.getResult().getDisplayName();
                    String mobile = outputUserInfoIMResult.getResult().getMobile();

                    Conversation conversation = new Conversation();
                    conversation.setType(mFwdType);
                    conversation.setTarget(mFwdTarget);

                    MessagePayload payload = new MessagePayload();
                    payload.setType(1);
                    payload.setSearchableContent("用户（" + displayName + " : " + mobile + "），发送了敏感词消息" );
                    MessageAdmin.sendMessage("admin", conversation, payload);
                    MessageAdmin.sendMessage("admin", conversation, outputMessageData.getPayload());

                    MessagePayload notifyPayload = new MessagePayload();
                    notifyPayload.setType(90);
                    notifyPayload.setContent(mNotificationText);
                    MessageAdmin.sendMessage(outputMessageData.getSender(), outputMessageData.getConv(), notifyPayload);

                    MessagePayload notifyPayload2 = new MessagePayload();
                    notifyPayload2.setType(90);
                    notifyPayload2.setContent(mNotificationText2);
                    MessageAdmin.sendMessage(outputMessageData.getSender(), outputMessageData.getConv(), notifyPayload2);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
