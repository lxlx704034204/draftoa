package com.starsoft.oa.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import com.starsoft.core.controller.BaseAjaxController;
import com.starsoft.core.controller.BaseInterface;
import com.starsoft.core.entity.BaseObject;
import com.starsoft.core.entity.Users;
import com.starsoft.core.util.HttpUtil;
import com.starsoft.core.util.Page;
import com.starsoft.oa.domain.ChengbanDomain;
import com.starsoft.oa.domain.FuyiDomain;
import com.starsoft.oa.domain.LianReturnDomain;
import com.starsoft.oa.domain.LianshenpiDomain;
import com.starsoft.oa.domain.MotionDomain;
import com.starsoft.oa.entity.Chengban;
import com.starsoft.oa.entity.ChengbanRecord;
import com.starsoft.oa.entity.LianReturn;
import com.starsoft.oa.entity.Lianshenpi;
import com.starsoft.oa.entity.Motion;

/**
 * 
 * @Description 立案回复
 * @author 赵一好
 * @date 2016-11-18 上午11:04:40
 * 
 */
public class LianReturnController extends MyBaseAjaxController implements
		BaseInterface {

	@Autowired
	private LianReturnDomain lianReturnDomain;
	
	@Autowired
	private MotionDomain motionDomain;
	
	@Autowired
	private ChengbanDomain chengbanDomain;
	@Autowired
	private LianshenpiDomain lianshenpiDomain;
	@Autowired
	private FuyiDomain fuyiDomain;
	
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> model = HttpUtil.convertModel(request);
		Users user = (Users) request.getSession().getAttribute("SESSONUSER");
		Page page = HttpUtil.convertPage(request);
		DetachedCriteria criteria = lianReturnDomain.getCriteria(null);
		//criteria.add(Restrictions.eq("clr", user.getId()));
		criteria.add(Restrictions.lt("read_index", "2"));
		List<LianReturn> lianReturns = lianReturnDomain.queryByCriteria(criteria,page);
		List<Motion> motions = new ArrayList<Motion>();
		for (LianReturn lianReturn : lianReturns) {
			String motionId = lianReturn.getMotionId();
			Motion motion = (Motion) motionDomain.queryFirstByProperty("id",
					motionId);
			motions.add(motion);
		}
		// 查找附议人数
		for (Motion motion : motions) {
			// 查找附议总人数
			String fyrNum = fuyiDomain.findFuyiCount(motion.getId(), null) + "";
			motion.setFyrNum(fyrNum);
			// 查找附议赞成人数
			String agreeNum = fuyiDomain.findFuyiCount(motion.getId(), "1") + "";
			motion.setAgreeNum(agreeNum);
			
		}
		model.put("lists", motions);
		model.put("page", page);

		return new ModelAndView(this.getListPage(), model);
	}
	
	// 向数据库保存信息
	public void save(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
	}

	// 跳转到立案回复界面
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String motionId = HttpUtil.getString(request, "id", "");
		HttpSession session = request.getSession();
		removeSesMotRec(session, motionId);
		Map<String, Object> model = HttpUtil.convertModel(request);
		
		/*
		 * Users session_user = (Users)
		 * request.getSession().getAttribute("SESSONUSER");
		 */

		// 自动将信息封装到model中
		findMotionById(request, model);
		// 查找承办人
		/*
		 * DetachedCriteria criteria = chengbanDomain.getCriteria(null);
		 * criteria.add(Restrictions.eq("cbr", session_user.getId()));
		 * criteria.add(Restrictions.eq("motionId", motionId)); List<Chengban>
		 * chengbans = chengbanDomain.queryByCriteria(criteria);
		 * if(chengbans.size()>0){ Chengban chengban = (Chengban)
		 * chengbans.get(0); model.put("chengban", chengban); }
		 */

		// 修改承办回复表中未已读1
		// updateOperationMark(request,"1",motionId);
		LianReturn lianReturn = (LianReturn) this.baseDomain.queryFirstByProperty("motionId", motionId);
		if(lianReturn.getRead_index().equals("0")){
			lianReturn.setRead_index("1");
		}
		this.baseDomain.update(lianReturn);
		Chengban chengban = (Chengban) chengbanDomain.queryFirstByProperty("motionId", motionId);
		Lianshenpi lianshenpi = (Lianshenpi) lianshenpiDomain.queryFirstByProperty("motionId", motionId);
		model.put("lianshenpi", lianshenpi);
		model.put("chengban", chengban);
		return new ModelAndView(this.getEditPage(), model);

	}

	

	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		return null;
	}

	public ModelAndView read(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		return null;
	}

	public void update(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

	}

}
