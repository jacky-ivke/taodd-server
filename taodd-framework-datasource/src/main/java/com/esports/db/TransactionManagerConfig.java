package com.esports.db;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 通过AOP切面设置全局事务，拦截service包下面所有方法
 * 
 * @author jacky
 *
 */
@Configuration
public class TransactionManagerConfig {

	private static final int TX_METHOD_TIMEOUT = 5;

	private static final String AOP_POINTCUT_EXPRESSION = " execution(* com.esports..*.service..*.*(..))";

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Bean
	public TransactionInterceptor txAdvice() {
		/* 事务管理规则，声明具备事务管理的方法名 */
		NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();

		/* 当前存在事务就用当前的事务，当前不存在事务就创建一个新的事务 */
		RuleBasedTransactionAttribute readOnlyRule = new RuleBasedTransactionAttribute();
		/* 设置当前事务是否为只读事务，true为只读 */
		readOnlyRule.setReadOnly(true);
		/* PROPAGATION_NOT_SUPPORTED事务传播级别5，以非事务运行，如果当前存在事务，则把当前事务挂起 */
		readOnlyRule.setPropagationBehavior(TransactionDefinition.PROPAGATION_NOT_SUPPORTED);

		RuleBasedTransactionAttribute requireRule = new RuleBasedTransactionAttribute();
		/* 抛出异常后执行切点回滚 */
		requireRule.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
		/* PROPAGATION_REQUIRED:事务隔离性为1，若当前存在事务，则加入该事务；如果当前没有事务，则创建一个新的事务 */
		requireRule.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		/* 设置事务失效时间，如果超过5秒，则回滚事务 */
		requireRule.setTimeout(TX_METHOD_TIMEOUT);

		Map<String, TransactionAttribute> txMap = new HashMap<String, TransactionAttribute>();
		txMap.put("add*", requireRule);
		txMap.put("save*", requireRule);
		txMap.put("insert*", requireRule);
		txMap.put("update*", requireRule);
		txMap.put("delete*", requireRule);
		txMap.put("remove*", requireRule);

		txMap.put("get*", readOnlyRule);
		txMap.put("query*", readOnlyRule);
		txMap.put("find*", readOnlyRule);
		txMap.put("select*", readOnlyRule);
		source.setNameMap(txMap);
		TransactionInterceptor txAdvice = new TransactionInterceptor(transactionManager, source);
		return txAdvice;
	}

	@Bean
	public Advisor txAdviceAdvisor() {
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression(AOP_POINTCUT_EXPRESSION);
		return new DefaultPointcutAdvisor(pointcut, txAdvice());
	}

}
