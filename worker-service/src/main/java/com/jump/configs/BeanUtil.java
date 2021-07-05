package com.jump.configs;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public final class BeanUtil implements ApplicationContextAware {

  private static ApplicationContext context;

  @Override
  public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
    context = applicationContext;
  }

  public static <T> T getBean(Class<T> beanClass) {
    if (null == context) {
      //methode called before initalization done
      throw new ExceptionInInitializerError();
    }
    return context.getBean(beanClass);
  }

  public static <T> T getBean(final String addNewPodcastJob, final Class<T> jobClass) {
    if (null == context) {
      //methode called before initalization done
      throw new ExceptionInInitializerError();
    }
    return context.getBean(addNewPodcastJob, jobClass);
  }

}