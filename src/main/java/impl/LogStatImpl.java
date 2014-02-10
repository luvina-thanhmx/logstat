package impl;
import java.util.HashMap;

import org.jruby.embed.LocalContextScope;
import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;
import org.jruby.embed.osgi.OSGiScriptingContainer;
import org.osgi.framework.Bundle;

import service.LogStat;
/**
 * Implement of LogStat service
 * @author nguyenxuanluong
 *
 */
public class LogStatImpl implements LogStat{
	Bundle bundle;
	public LogStatImpl(Bundle bundle){
		this.bundle = bundle;
	}

	/**
	 * Monitoring logs
	 * @param args : An array of paramters 
	 */
	@Override
	public void runLogStat(HashMap<String,Object> conf) {
		try {
			LogStatBean bean = new LogStatBean();
			ScriptingContainer container = new OSGiScriptingContainer(this.bundle,LocalContextScope.SINGLETHREAD,LocalVariableBehavior.PERSISTENT);
			container.setHomeDirectory("classpath:/META-INF/jruby.home");
			System.out.println("LogStartService Running ...");

			bean.setConfig(conf);
			container.put("bean", bean);
			System.out.println(bean.getConfig().toString());
			container.runScriptlet("require 'ruby/ProcessInput.rb'");
			container.runScriptlet("require 'ruby/ProcessFilter.rb'");
			container.runScriptlet("require 'ruby/ProcessOutput.rb'");
			//Get input logs from source
			container.runScriptlet("pi = ProcessInput.new");
			container.runScriptlet("bean.setInput(pi.getInputData((bean.getConfig)['input']))");
			//Filter logs
			container.runScriptlet("pf = ProcessFilter.new");
			container.runScriptlet("bean.setOutput(pf.filter(bean.getInput,(bean.getConfig)['filter']))");
			//Output logs
			container.runScriptlet("po = ProcessOutput.new");
			container.runScriptlet("po.output(bean.getOutput,(bean.getConfig)['output'])");
			
			System.out.println("LogStartService Completed ...");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		
	}
	//Bean to store logstat information (input-output data & configuration)
	public class LogStatBean {
		public Object getInput() {
			return input;
		}
		public void setInput(Object input) {
			this.input = input;
		}
		public Object getOutput() {
			return output;
		}
		public void setOutput(Object output) {
			this.output = output;
		}
		public HashMap<String,Object> getConfig() {
			return config;
		}
		public void setConfig(HashMap<String,Object> config) {
			
			this.config = config;
		}
		Object input;
		Object  output;
		public HashMap<String,Object> config;
		
	}

}
