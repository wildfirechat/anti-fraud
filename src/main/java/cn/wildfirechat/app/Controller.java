package cn.wildfirechat.app;

import cn.wildfirechat.pojos.OutputMessageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    @Autowired
    private Service mService;

    @PostMapping(value = "/sensitive_message")
    public Object onMessage(@RequestBody OutputMessageData event) {
        mService.onReceiveMessage(event);
        return "ok";
    }
}
