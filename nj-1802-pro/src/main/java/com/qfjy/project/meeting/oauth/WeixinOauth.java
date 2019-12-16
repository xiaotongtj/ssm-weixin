package com.qfjy.project.meeting.oauth;

import com.qfjy.project.weixin.main.MenuManager;
import com.qfjy.project.weixin.util.WeixinUtil;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

/**
 * 微信公众号授权
 */
@RestController
@RequestMapping("weixin")
public class WeixinOauth {

    /**
     * 第一步同意授权，获取code
     */
    @RequestMapping("oauth")
    public void oauth(HttpServletResponse response) throws Exception {
        String path = MenuManager.REAL_URL + "weixin/invoke"; //重点
        path = URLEncoder.encode(path, "UTF-8");
        //todo 这一步就是弹窗授权页面
        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?" +
                "appid=" + MenuManager.appId +
                "&redirect_uri=" + path +
                "&response_type=code&" +
                "scope=snsapi_userinfo&" +
                "state=java_tongjian" +
                "#wechat_redirect";
        response.sendRedirect(url);
    }

    //如果用户同意授权，页面将跳转至 redirect_uri/?code=CODE&state=STATE。
    @RequestMapping("invoke")//域名 + weixin/invoke
    public void oauthInvoke(HttpServletRequest request) {
        //2.获取code
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        System.out.println(code + "---" + state);
        //3.通过code获取到access_token
        String url =
                "https://api.weixin.qq.com/sns/oauth2/access_token?" +
                        "appid=" + MenuManager.appId +
                        "&secret=" + MenuManager.appSecret +
                        "&code=" + code +
                        "&grant_type=authorization_code";
        //认证服务器
        JSONObject jsonObject = WeixinUtil.httpRequest(url, "POST", null);
        System.out.println(jsonObject.toString());

        //openId + access_token 获取资源信息
        String urlUserInfo = "https://api.weixin.qq.com/sns/userinfo?" +
                "access_token=" +jsonObject.getString("access_token")+
                "&openid=" + jsonObject.getString("openid")+
                "&lang=zh_CN";

        JSONObject urlJson = WeixinUtil.httpRequest(urlUserInfo, "GET", null);
        System.out.println(urlJson.toString());

    }
}
