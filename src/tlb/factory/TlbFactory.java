package tlb.factory;

import tlb.TlbConstants;
import tlb.server.ServerInitializer;
import tlb.server.TlbServerInitializer;
import tlb.service.TalkToService;
import tlb.splitter.TalksToService;
import tlb.utils.SystemEnvironment;
import tlb.splitter.TestSplitterCriteria;
import tlb.splitter.JobFamilyAwareSplitterCriteria;
import tlb.orderer.TestOrderer;

import java.lang.reflect.InvocationTargetException;

/**
 * @understands creating a criteria based on the class
 */
public class TlbFactory<T> {
    private Class<T> klass;
    private T defaultValue;
    private static TlbFactory<TestSplitterCriteria> criteriaFactory;
    private static TlbFactory<TestOrderer> testOrderer;
    private static TlbFactory<TalkToService> talkToServiceFactory;
    private static TlbFactory<ServerInitializer> restletLauncherFactory;

    TlbFactory(Class<T> klass, T defaultValue) {
        this.klass = klass;
        this.defaultValue = defaultValue;
    }

    public <T> T getInstance(String klassName, SystemEnvironment environment) {
        if (klassName == null || klassName.isEmpty()) {
            return (T) defaultValue;
        }
        try {
            Class<?> criteriaClass = Class.forName(klassName);
            if(!klass.isAssignableFrom(criteriaClass)) {
                throw new IllegalArgumentException("Class '" + klassName + "' is-not/does-not-implement '" + klass + "'");
            }
            return getInstance((Class<? extends T>) criteriaClass, environment);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Unable to locate class '" + klassName + "'");
        }
    }

    <T> T getInstance(Class<? extends T> actualKlass, SystemEnvironment environment) {
        try {
            T criteria = actualKlass.getConstructor(SystemEnvironment.class).newInstance(environment);
            if (TalksToService.class.isInstance(criteria)) {
                TalkToService service = getTalkToService(environment);
                ((TalksToService)criteria).talksToService(service);
            }
            return criteria;
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Public constructor matching " + actualKlass.getName() + "(SystemEnvironment) was not found", e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Public constructor matching " + actualKlass.getName() + "(SystemEnvironment) was not found", e);
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("Unable to create abstract class " + actualKlass.getName(), e);
        }
    }

    public static TestSplitterCriteria getCriteria(String criteriaName, SystemEnvironment environment) {
        if (criteriaFactory == null)
            criteriaFactory = new TlbFactory<TestSplitterCriteria>(TestSplitterCriteria.class, JobFamilyAwareSplitterCriteria.MATCH_ALL_FILE_SET);
        return criteriaFactory.getInstance(criteriaName, environment);
    }

    public static TestOrderer getOrderer(String ordererName, SystemEnvironment environment) {
        if (testOrderer == null)
            testOrderer = new TlbFactory<TestOrderer>(TestOrderer.class, TestOrderer.NO_OP);
        return testOrderer.getInstance(ordererName, environment);
    }

    public static TalkToService getTalkToService(SystemEnvironment environment) {
        if (talkToServiceFactory == null)
            talkToServiceFactory = new TlbFactory<TalkToService>(TalkToService.class, null);
        return talkToServiceFactory.getInstance(environment.getProperty(TlbConstants.TALK_TO_SERVICE), environment);
    }

    public static ServerInitializer getRestletLauncher(String restletLauncherName, SystemEnvironment environment) {
        if (restletLauncherFactory == null)
            restletLauncherFactory = new TlbFactory<ServerInitializer>(ServerInitializer.class, new TlbServerInitializer(environment));
        return restletLauncherFactory.getInstance(restletLauncherName, environment);
    }
}
