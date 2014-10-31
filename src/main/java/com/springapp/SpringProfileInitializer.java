package com.springapp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudException;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.service.ServiceInfo;
import org.springframework.cloud.service.common.*;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

import java.util.*;

public class SpringProfileInitializer  implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Log logger = LogFactory.getLog(SpringProfileInitializer.class);
    private static final Map<Class<? extends ServiceInfo>, String> serviceTypeToProfileName =
            new HashMap<Class<? extends ServiceInfo>, String>();
    private static final List<String> validLocalProfiles = Arrays.asList("redis");


    static {
        serviceTypeToProfileName.put(RedisServiceInfo.class, "redis");
    }


    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Cloud cloud = getCloud();
        ConfigurableEnvironment appEnvironment = applicationContext.getEnvironment();

        String[] persistenceProfiles = getCloudProfile(cloud);
        if (persistenceProfiles == null) {
            persistenceProfiles = getActiveProfile(appEnvironment);
        }
        if (persistenceProfiles == null) {
            persistenceProfiles = new String[] { "redis", "redis-local" };
        }
        if(persistenceProfiles!=null){
            for (String persistenceProfile : persistenceProfiles) {
                appEnvironment.addActiveProfile(persistenceProfile);
                logger.info("### active profile:"+persistenceProfile);

            }
        }

        if(cloud!=null){
            for(ServiceInfo sv: cloud.getServiceInfos()){
                logger.info("### cloud ENV id:"+sv.getId());
                logger.info("### cloud ENV class:"+sv.getClass());

            }
        }
    }


    private Cloud getCloud() {
        try {
            CloudFactory cloudFactory = new CloudFactory();
            return cloudFactory.getCloud();
        } catch (CloudException ce) {
            return null;
        }
    }



    public String[] getCloudProfile(Cloud cloud) {
        if (cloud == null) {
            return null;
        }

        List<String> profiles = new ArrayList<String>();

        List<ServiceInfo> serviceInfos = cloud.getServiceInfos();

        logger.info("Found serviceInfos: " + StringUtils.collectionToCommaDelimitedString(serviceInfos));

        for (ServiceInfo serviceInfo : serviceInfos) {
            if (serviceTypeToProfileName.containsKey(serviceInfo.getClass())) {
                profiles.add(serviceTypeToProfileName.get(serviceInfo.getClass()));
            }
        }

        if (profiles.size() > 1) {
            throw new IllegalStateException(
                    "Only one service of the following types may be bound to this application: " +
                            serviceTypeToProfileName.values().toString() + ". " +
                            "These services are bound to the application: [" +
                            StringUtils.collectionToCommaDelimitedString(profiles) + "]");
        }

        if (profiles.size() > 0) {
            return createProfileNames(profiles.get(0), "cloud");
        }

        return null;
    }



    private String[] getActiveProfile(ConfigurableEnvironment appEnvironment) {
        List<String> serviceProfiles = new ArrayList<String>();

        for (String profile : appEnvironment.getActiveProfiles()) {
            if (validLocalProfiles.contains(profile)) {
                serviceProfiles.add(profile);
            }
        }

        if (serviceProfiles.size() > 1) {
            throw new IllegalStateException("Only one active Spring profile may be set among the following: " +
                    validLocalProfiles.toString() + ". " +
                    "These profiles are active: [" +
                    StringUtils.collectionToCommaDelimitedString(serviceProfiles) + "]");
        }

        if (serviceProfiles.size() > 0) {
            return createProfileNames(serviceProfiles.get(0), "local");
        }

        return null;
    }

    private String[] createProfileNames(String baseName, String suffix) {
        String[] profileNames = {baseName, baseName + "-" + suffix};
        logger.info("Setting profile names: " + StringUtils.arrayToCommaDelimitedString(profileNames));
        return profileNames;
    }

}
