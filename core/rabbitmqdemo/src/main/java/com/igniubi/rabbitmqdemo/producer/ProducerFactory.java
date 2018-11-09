package com.igniubi.rabbitmqdemo.producer;

import com.igniubi.rabbitmqdemo.properties.ConnectionInfo;
import com.igniubi.rabbitmqdemo.properties.ProducerInfo;
import com.igniubi.rabbitmqdemo.properties.RabbitYmlFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.PriorityOrdered;

import java.util.List;

/**
 * <p>
 * construct IProducer and register bean in spring
 */
public class ProducerFactory implements BeanFactoryPostProcessor, PriorityOrdered {
    private static final Logger logger = LoggerFactory.getLogger(ProducerFactory.class);

    /**
     * construct producers
     */
    /**
     * Modify the application context's internal bean factory after its standard
     * initialization. All bean definitions will have been loaded, but no beans
     * will have been instantiated yet. This allows for overriding or adding
     * properties even to eager-initializing beans.
     *
     * @param beanFactory the bean factory used by the application context
     * @throws BeansException in case of errors
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        logger.info("ProducerFactory: register rabbitmq-producer-wrapper define into spring");
        //fetch producerInfo list
        List<ProducerInfo> producerInfoList = RabbitYmlFactory.getProducerInfoList();

        //construct producer
        for (ProducerInfo oneProducer : producerInfoList) {
            BeanDefinitionBuilder producerBuilder;
            ConnectionInfo rabbitConnection = oneProducer.getConnection();

//            //register rabbitTemplate bean
            BeanDefinitionBuilder rabbitTemplateBuilder = BeanDefinitionBuilder.genericBeanDefinition(RabbitTemplate.class);
            rabbitTemplateBuilder.addPropertyReference("connectionFactory", rabbitConnection.getFactoryId());
            rabbitTemplateBuilder.addPropertyReference("messageConverter", "rabbit-simpleMessageConverter");
            String rabbitTempleBeanId = rabbitConnection.getTemplateId();
            ((DefaultListableBeanFactory) beanFactory).registerBeanDefinition(rabbitTempleBeanId, rabbitTemplateBuilder.getBeanDefinition());

            //construct bean builder, register bean
            producerBuilder = oneProducer.isAsync() ? BeanDefinitionBuilder.genericBeanDefinition(AsyncProducerWrapper.class) : BeanDefinitionBuilder.genericBeanDefinition(ProducerWrapper.class);
            producerBuilder.addPropertyValue("producerInfo", oneProducer);
            producerBuilder.addPropertyValue("beanFactory", ((DefaultListableBeanFactory) beanFactory));
            producerBuilder.addPropertyReference("rabbitTemplate", rabbitConnection.getTemplateId());
            ((DefaultListableBeanFactory) beanFactory).registerBeanDefinition(oneProducer.getBeanName(), producerBuilder.getBeanDefinition());

            logger.info("register rabbitmq-producer-wrapper:{} into spring define map....", oneProducer.getBeanName());

        }
    }

    @Override
    public int getOrder() {
        //make it as early as possible
        return 2;
    }
}