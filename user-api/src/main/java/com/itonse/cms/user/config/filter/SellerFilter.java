package com.itonse.cms.user.config.filter;

import com.itonse.cms.domain.config.JwtAuthenticationProvider;
import com.itonse.cms.domain.domain.common.UserVo;
import com.itonse.cms.user.service.seller.SellerService;
import lombok.RequiredArgsConstructor;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(urlPatterns = "/seller/*")  // 필터가 적용 될 url 패턴
@RequiredArgsConstructor
public class SellerFilter implements Filter {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final SellerService sellerService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String token = req.getHeader("X-AUTH-TOKEN");

        if (!jwtAuthenticationProvider.validateToken(token)) {
            throw new ServletException("Invalid Access");
        }

        UserVo vo = jwtAuthenticationProvider.getUserVo(token);
        sellerService.findByIdAndEmail(vo.getId(), vo.getEmail())
                .orElseThrow(() -> new ServletException("Invalid access"));

        // 필터에서 내보내기 ( 그 다음으로 전송)
        chain.doFilter(request, response);
    }
}
