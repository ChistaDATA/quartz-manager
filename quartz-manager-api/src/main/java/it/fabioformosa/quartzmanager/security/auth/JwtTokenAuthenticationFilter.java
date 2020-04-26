package it.fabioformosa.quartzmanager.security.auth;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import it.fabioformosa.quartzmanager.security.JwtTokenHelper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

  public static final String ROOT_MATCHER = "/";
  public static final String FAVICON_MATCHER = "/favicon.ico";
  public static final String HTML_MATCHER = "/**/*.html";
  public static final String CSS_MATCHER = "/**/*.css";
  public static final String JS_MATCHER = "/**/*.js";
  public static final String IMG_MATCHER = "/images/*";
  public static final String LOGIN_MATCHER = "/api/login";
  public static final String LOGOUT_MATCHER = "/api/logout";

  //	private final Log logger = LogFactory.getLog(this.getClass());

  //  @Autowired
  private final JwtTokenHelper jwtTokenHelper;

  //  @Autowired
  private final UserDetailsService userDetailsService;

  private List<String> pathsToSkip = Arrays.asList(
      ROOT_MATCHER,
      HTML_MATCHER,
      FAVICON_MATCHER,
      CSS_MATCHER,
      JS_MATCHER,
      IMG_MATCHER,
      LOGIN_MATCHER,
      LOGOUT_MATCHER
      );


  public JwtTokenAuthenticationFilter(JwtTokenHelper jwtTokenHelper, UserDetailsService userDetailsService) {
    super();
    this.jwtTokenHelper = jwtTokenHelper;
    this.userDetailsService = userDetailsService;
  }

  @Override
  public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

    String jwtToken = jwtTokenHelper.retrieveToken(request);
    if (jwtToken != null && !skipPathRequest(request, pathsToSkip))
      try {
        String username = jwtTokenHelper.getUsernameFromToken(jwtToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        // create authentication
        JwtTokenBasedAuthentication authentication = new JwtTokenBasedAuthentication(userDetails);
        authentication.setToken(jwtToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
      } catch (Exception e) {
        SecurityContextHolder.getContext().setAuthentication(new AnonAuthentication());
        log.error("Switched to Anonimous Authentication, "
            + "because an error occurred setting authentication in security context holder due to " + e.getMessage(), e);
      }
    else
      SecurityContextHolder.getContext().setAuthentication(new AnonAuthentication());

    chain.doFilter(request, response);
  }

  private boolean skipPathRequest(HttpServletRequest request, List<String> pathsToSkip ) {
    Assert.notNull(pathsToSkip, "path cannot be null.");
    List<RequestMatcher> m = pathsToSkip.stream().map(path -> new AntPathRequestMatcher(path)).collect(Collectors.toList());
    OrRequestMatcher matchers = new OrRequestMatcher(m);
    return matchers.matches(request);
  }

}