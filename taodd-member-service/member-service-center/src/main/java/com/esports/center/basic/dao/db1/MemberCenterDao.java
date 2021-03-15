package com.esports.center.basic.dao.db1;

import com.esports.center.basic.bean.db1.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MemberCenterDao extends JpaRepository<Member, Long>, JpaSpecificationExecutor<Member> {

    /**
     * 根据邮箱查询会员信息
     */
    Member findByEmail(String email);

    /**
     * 根据微信号查询会员信息
     */
    Member findByWechat(String wechat);

    /**
     * 根据QQ号查询会员信息
     */
    Member findByQq(String qq);

    /**
     * 根据账号查询会员信息
     */
    Member findByAccount(String account);

    /**
     * 根据手机号查询会员信息
     */
    Member findByMobile(String mobile);

    /**
     * 根据邀请码查询会员信息
     */
    Member findByInviteCode(String inviteCode);

    /**
     * 一级好友
     */
    @Query(value = "select t.account,t.reg_time as regTime from tb_player t where t.type=1 and t.inviter=?1",
            nativeQuery = true)
    List<Map<String, Object>> getFirstFriends(String account);

    /**
     * 二级好友
     */
    @Query(
            value = "select t2.account,t2.reg_time as regTime from tb_player t2 where t2.inviter in (select t1.account from tb_player t1 where t1.inviter=?1)",
            nativeQuery = true)
    List<Map<String, Object>> getSecondFriends(String account);

    /**
     * 三级好友
     */
    @Query(
            value = "select t3.account,t3.reg_time as regTime from tb_player t3 where t3.inviter in(select t2.account from tb_player t2 where t2.inviter in(select t1.account from tb_player t1 where t1.inviter=?1))",
            nativeQuery = true)
    List<Map<String, Object>> getThirdFriends(String account);

    /**
     * 会员交易账单类型
     */
    @Query(nativeQuery = true, value = "select v.v_name as title,v.v_value as type from sys_code_table t " +
            "inner join sys_code_table_values v on t.id = v.fk_code_table_id where t.t_code = 'member_bill_type' and v.v_name is not null and v.v_name <> '' order by v.v_order ")
    List<Map<String, Object>> getTradeBillType();

    /**
     * 会员系统游戏类型
     */
    @Query(nativeQuery = true, value = "select v.v_name as name,v.v_value as gameType from sys_code_table t " +
            "inner join sys_code_table_values v on t.id = v.fk_code_table_id " +
            "where t.t_code = 'dict_game_type' and v.v_value <> '' order by v.v_order ")
    List<Map<String, Object>> getGameTypes();

    /**
     * 会员反水方案
     */
    @Query(nativeQuery = true, value = "select s1.scheme_code from tb_grade_scheme s left join tb_grade_rake r on s.id = r.grade_id left join tb_rakeback_scheme s1 on r.rake_id = s1.id where s.scheme_code=?1")
    String getRakeScheme(String gradeCode);

    /**
     * 会员提款方案
     */
    @Query(nativeQuery = true, value = "select l.scheme_code from tb_draw_scheme l inner join tb_grade_draw r on l.id = r.draw_id inner join tb_grade_scheme s on r.grade_id = s.id"
            + " inner join tb_player p on p.grade_code = s.scheme_code"
            + " where p.account =?1")
    String getDrawScheme(String account);
}
