package com.opu.opube.feature.auth.command.application.util;

public class EmailHtmlBuilder {

    public static String buildVerificationHtml(String nickname, String verifyUrl, String iconUrl) {
        return """
<html>
<body style="margin:0; padding:0; background:#f8f9fc;
             font-family:'Apple SD Gothic Neo','Noto Sans KR',sans-serif;">
  <div style="max-width:480px; margin:40px auto; background:#fff; border-radius:12px;
              padding:32px 24px; box-shadow:0 4px 12px rgba(0,0,0,0.06);">

    <img src="%s" alt="OPU Icon"
         style="width:144px; height:144px; border-radius:16px; display:block; margin:0 auto 16px;" />


    <p style="font-size:15px; color:#555; text-align:center; margin-bottom:24px; line-height:1.5;">
      <span style="font-weight:700; color:#B8DD7C;">%s</span> 님, 환영합니다! 🍀<br/>
      아래 버튼을 눌러 계정 인증을 완료해주세요.
    </p>

    <a href="%s" target="_blank"
       style="display:block; width:100%%; background:#B8DD7C; color:#fff;
              text-decoration:none; padding:14px 0; border-radius:8px;
              font-size:16px; font-weight:600; text-align:center;
              box-shadow:0 2px 6px rgba(47,128,237,0.3);">
      이메일 인증하기
    </a>

    <hr style="border:none; border-top:1px solid #eee; margin:24px 0;" />

    <p style="font-size:12px; color:#aaa; text-align:center; margin:0;">
      본 메일은 발신 전용입니다.<br/>
      © 2025 OPU. All rights reserved.
    </p>

  </div>
</body>
</html>
""".formatted(iconUrl, nickname, verifyUrl);
    }

    public static String buildPasswordResetHtml(String nickname, String resetUrl, String iconUrl) {
        return """
<html>
<body style="margin:0; padding:0; background:#f8f9fc; font-family: 'Apple SD Gothic Neo', 'Noto Sans KR', sans-serif;">
  <div style="max-width:480px; margin:40px auto; background:#fff; border-radius:12px; padding:32px 24px;
              box-shadow:0 4px 12px rgba(0,0,0,0.06);">

    <img src="%s" alt="OPU Icon"
      style="width:144px; height:144px; border-radius:16px; display:block; margin:0 auto 16px;" />


    <p style="font-size:15px; color:#555; text-align:center; margin-bottom:24px; line-height:1.5;">
      <span style="font-weight:700; color:#B8DD7C;">%s</span> 님, 비밀번호 재설정 요청이 접수되었습니다.<br/>
      아래 버튼을 눌러 새 비밀번호를 설정해주세요.
    </p>

    <a href="%s" target="_blank"
       style="display:block; width:100%%; background:#B8DD7C; color:#fff;
              text-decoration:none; padding:14px 0; border-radius:8px;
              font-size:16px; font-weight:600; text-align:center;">
      비밀번호 재설정
    </a>

    <p style="font-size:12px; color:#999; text-align:center; margin-top:16px;">
      만약 본인이 요청하지 않았다면, 이 메일은 무시하셔도 됩니다.
    </p>

    <hr style="border:none; border-top:1px solid #eee; margin:24px 0;" />

    <p style="font-size:12px; color:#aaa; text-align:center; margin:0;">
      본 메일은 발신 전용입니다.<br/>
      © 2025 OPU. All rights reserved.
    </p>

  </div>
</body>
</html>
""".formatted(iconUrl, nickname, resetUrl);
    }
}

