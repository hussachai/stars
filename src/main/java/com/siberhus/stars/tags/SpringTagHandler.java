package com.siberhus.stars.tags;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;

import net.sourceforge.stripes.util.ReflectUtil;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.siberhus.stars.ServiceProvider;
import com.siberhus.stars.stripes.StarsConfiguration;

public class SpringTagHandler extends ScopedBeanTagSupport {

	private String name;

	private String type;
	
	@Override
	public int doStartTag() throws JspException {
		ServletContext servletContext = getPageContext().getServletContext();
		StarsConfiguration starsConfig = StarsConfiguration.get(servletContext);
		if(ServiceProvider.SPRING!=starsConfig.getServiceProvider()){
			return SKIP_BODY;
		}
		Object bean = getBean();
		if(bean==null){
			ApplicationContext springContext = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			if (name != null) {
				bean = springContext.getBean(name);
			} else if (type != null) {
				try {
					Class<?> typeClass = ReflectUtil.findClass(type);
					if(!typeClass.isInterface()){
						throw new IllegalArgumentException("Type attribute must be interface class");
					}
					bean = springContext.getBean(typeClass);
				} catch (ClassNotFoundException e) {
					throw new JspException(e);
				}
			} else {
				throw new IllegalArgumentException(
						"name or type attribute is required!");
			}
			setBean(bean);
		}
		return super.doStartTag();
	}

	@Override
	public void release() {
		super.release();
		this.name = null;
		this.type = null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
	
}
