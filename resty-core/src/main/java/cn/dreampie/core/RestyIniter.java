package cn.dreampie.core;

import cn.dreampie.core.base.Plugin;
import cn.dreampie.core.config.Config;
import cn.dreampie.core.config.ConstantLoader;
import cn.dreampie.core.handler.Handler;
import cn.dreampie.core.handler.HandlerFactory;
import cn.dreampie.core.route.RouteBuilder;
import cn.dreampie.core.route.RouteHandler;
import cn.dreampie.log.Logger;
import cn.dreampie.log.LoggerFactory;

import javax.servlet.ServletContext;
import java.util.List;

/**
 * Restj
 */
public final class RestyIniter {

  private static final Logger logger = LoggerFactory.getLogger(RestyIniter.class);

  private ConstantLoader constantLoader;
  private RouteBuilder routeBuilder;
  private Handler handler;
  private ServletContext servletContext;

  public Handler getHandler() {
    return handler;
  }

  private static final RestyIniter instance = new RestyIniter();

  private RestyIniter() {
  }

  public static RestyIniter instance() {
    return instance;
  }

  public boolean init(Config config, ServletContext servletContext) {
    this.servletContext = servletContext;

    ConfigIniter.config(config);  // start plugin and init logger factory in this method
    constantLoader = ConfigIniter.getConstantLoader();

    initRoutes();
    initHandler();

    return true;
  }


  private void initHandler() {
    Handler routeHandler = new RouteHandler(routeBuilder);
    handler = HandlerFactory.getHandler(ConfigIniter.getHandlerLoader().getHandlerList(), routeHandler);
  }


  private void initRoutes() {
    ConfigIniter.getResourceLoader().build();
    routeBuilder = new RouteBuilder(ConfigIniter.getResourceLoader(), ConfigIniter.getInterceptorLoader());
    routeBuilder.build();
  }

  public void stopPlugins() {
    List<Plugin> plugins = ConfigIniter.getPluginLoader().getPluginList();
    if (plugins != null) {
      for (int i = plugins.size() - 1; i >= 0; i--) {    // stop plugins

        try {
          if (!plugins.get(i).stop())
            logger.error("Plugin stop error: " + plugins.get(i).getClass().getName());
        } catch (Exception e) {
          logger.error("Plugin stop error: " + plugins.get(i).getClass().getName(), e);
        }
      }
    }
  }

  public ServletContext getServletContext() {
    return this.servletContext;
  }

  public ConstantLoader getConstantLoader() {
    return ConfigIniter.getConstantLoader();
  }

}









