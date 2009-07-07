package railo.runtime.tag;


import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.BodyTagImpl;
import railo.runtime.net.mail.MailException;
import railo.runtime.net.mail.MailPart;
import railo.runtime.net.smtp.SMTPClient;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
// TODO test proxy
/**
 * 
* Sends e-mail messages by an SMTP server.
*
*
*
**/
public final class Mail extends BodyTagImpl {

	/** Specifies the query column to use when you group sets of records together to send as an e-mail 
	** 		message. For example, if you send a set of billing statements to customers, you might group on 
	** 		"Customer_ID." The group attribute, which is case sensitive, eliminates adjacent duplicates when the 
	** 		data is sorted by the specified field. See the Usage section for exceptions. */
	private String group;

	/** Boolean indicating whether to group with regard to case or not. The default value is YES; 
	** 		case is considered while grouping. If the query attribute specifies a query object that was generated 
	** 		by a case-insensitive SQL query, set the groupCaseSensitive attribute to NO to keep the recordset 
	** 		intact. */
	private boolean groupcasesensitive;

	/** The name of the cfquery from which to draw data for message(s) to send. Specify this 
	** 		attribute to send more than one mail message, or to send the results of a query within a message. */
	private String query;

	/** Specifies the maximum number of e-mail messages to send. */
	private double maxrows;

	/** Specifies the row in the query to start from. */
	private double startrow;
    


	//private railo.runtime.mail.Mail mail=new railo.runtime.mail.Mail();
	private SMTPClient smtp=new SMTPClient();
	private railo.runtime.net.mail.MailPart part=null;//new railo.runtime.mail.MailPart("UTF-8");

	private String charset;

	private int priority;
	
	

	/**
	* @see javax.servlet.jsp.tagext.Tag#release()
	*/
	public void release()	{
		super.release();
//       do not clear because spooler
        //mail=new railo.runtime.mail.Mail();	
		smtp=new SMTPClient();
        part=null;//new railo.runtime.mail.MailPart("UTF-8");
		group=null;
		groupcasesensitive=false;
		query=null;
		maxrows=0d;
		startrow=0d;
		charset=null;
	}
	
	/**
     * @param proxyserver The proxyserver to set.
	 * @throws ApplicationException 
     */
    public void setProxyserver(String proxyserver) throws ApplicationException {
		try {
			smtp.getProxyData().setServer(proxyserver);
		} catch (Exception e) {
			throw new ApplicationException("invalid definition for attribute proxyserver at tag mail",e.getMessage());
		}
    }
	
	/** set the value proxyport
	*  The port number on the proxy server from which the object is requested. Default is 80. When 
	* 	used with resolveURL, the URLs of retrieved documents that specify a port number are automatically 
	* 	resolved to preserve links in the retrieved document.
	* @param proxyport value to set
	 * @throws ApplicationException 
	**/
	public void setProxyport(double proxyport) throws ApplicationException	{
		try {
			smtp.getProxyData().setPort((int)proxyport);
		} catch (Exception e) {
			throw new ApplicationException("invalid definition for attribute proxyport at tag mail",e.getMessage());
		}
	}

	/** set the value username
	*  When required by a proxy server, a valid username.
	* @param proxyuser value to set
	 * @throws ApplicationException 
	**/
	public void setProxyuser(String proxyuser) throws ApplicationException	{
		try {
			smtp.getProxyData().setUsername(proxyuser);
		} catch (Exception e) {
			throw new ApplicationException("invalid definition for attribute proxyuser at tag mail",e.getMessage());
		}
	}

    
	/** set the value password
	*  When required by a proxy server, a valid password.
	* @param proxypassword value to set
	 * @throws ApplicationException 
	**/
	public void setProxypassword(String proxypassword) throws ApplicationException	{
		try {
			smtp.getProxyData().setPassword(proxypassword);
		} catch (Exception e) {
			throw new ApplicationException("invalid definition for attribute proxypassword at tag mail",e.getMessage());
		}
	}

	

	/** set the value from
	*  The sender of the e-mail message.
	* @param strForm value to set
	 * @throws ApplicationException
	**/
	public void setFrom(String strForm) throws ApplicationException	{
		try {
			smtp.setFrom(strForm);
		} catch (Exception e) {
			throw new ApplicationException("invalid definition for attribute from at tag mail",e.getMessage());
		}
	}

	/** set the value to
	*  The name of the e-mail message recipient.
	* @param strTo value to set
	 * @throws ApplicationException
	**/
	public void setTo(String strTo) throws ApplicationException	{
		try {
			smtp.addTo(strTo);
		} catch (Exception e) {
			throw new ApplicationException("invalid definition for attribute to at tag mail",e.getMessage());
		}
	}

	/** set the value cc
	*  Indicates addresses to copy the e-mail message to; "cc" stands for "carbon copy."
	* @param strCc value to set
	 * @throws ApplicationException
	**/
	public void setCc(String strCc) throws ApplicationException	{
		try {
			smtp.addCC(strCc);
		} catch (Exception e) {
			throw new ApplicationException("invalid definition for attribute cc at tag mail",e.getMessage());
		}
	}

	/** set the value bcc
	*  Indicates addresses to copy the e-mail message to, without listing them in the message header. 
	* 		"bcc" stands for "blind carbon copy."
	* @param strBcc value to set
	 * @throws ApplicationException
	**/
	public void setBcc(String strBcc) throws ApplicationException	{
		try {
			smtp.addBCC(strBcc);
		} catch (Exception e) {
			throw new ApplicationException("invalid definition for attribute bcc at tag mail",e.getMessage());
		}
	}	
	
	/**
	 * @param strFailto The failto to set.
	 * @throws ApplicationException
	 */
	public void setFailto(String strFailto) throws ApplicationException {
		try {
			smtp.addFailTo(strFailto);
		} catch (Exception e) {
			throw new ApplicationException("invalid definition for attribute failto at tag mail",e.getMessage());
		}
	}
	/**
	 * @param strReplyto The replyto to set.
	 * @throws ApplicationException
	 */
	public void setReplyto(String strReplyto) throws ApplicationException {
		try {
			smtp.addReplyTo(strReplyto);
		} catch (Exception e) {
			throw new ApplicationException("invalid definition for attribute replyto at tag mail",e.getMessage());
		}
	}
	
	/** set the value type
	*  Specifies extended type attributes for the message.
	* @param type value to set
	 * @throws ApplicationException
	**/
	public void setType(String type) throws ApplicationException	{
		type=type.toLowerCase().trim();
		if(type.equals("text/plain") || type.equals("plain") || type.equals("text"))
		    getPart().isHTML(false);
		    //mail.setType(railo.runtime.mail.Mail.TYPE_TEXT);
		else if(type.equals("text/html") || type.equals("html") || type.equals("htm"))
			getPart().isHTML(true);
		else
			throw new ApplicationException("attribute type of tag mail has a invalid values","valid values are [plain,text,html] but value is now ["+type+"]");
			//throw new ApplicationException(("invalid type "+type);
	}

	/** set the value subject
	*  The subject of the mail message. This field may be driven dynamically on 
	* 		a message-by-message basis
	* @param subject value to set
	**/
	public void setSubject(String subject)	{
		smtp.setSubject(subject);
	}
	/**
	 * @param username The username to set.
	 */
	public void setUsername(String username) {
		smtp.setUsername(username);
	}
	/**
	 * @param password The password to set.
	 */
	public void setPassword(String password) {
		smtp.setPassword(password);
	}
	
	/** set the value mimeattach
	*  Specifies the path of the file to be attached to the e-mail message. An attached file 
	* 		is MIME-encoded.
	* @param strMimeattach value to set
	 * @param type mimetype of the file
	 * @param contentID 
	 * @param disposition 
	 * @throws PageException 
	**/
	public void setMimeattach(String strMimeattach, String type, String disposition, String contentID) throws PageException	{
		Resource file=ResourceUtil.toResourceNotExisting(pageContext,strMimeattach);
        pageContext.getConfig().getSecurityManager().checkFileLocation(file);
		if(!file.exists())
			throw new ApplicationException("can't attach file "+strMimeattach+", this file doesn't exist");
		

        //try {
			smtp.addAttachment(file,type,disposition,contentID);
		/*} catch (MessagingException e) {
			throw new ApplicationException("there is a problem with the attachment",e.getMessage());
		}*/
        
	}
	public void setMimeattach(String strMimeattach) throws PageException	{
		setMimeattach(strMimeattach, "", null, null);
	}
	
	/**
	 * @param spoolenable The spoolenable to set.
	 */
	public void setSpoolenable(boolean spoolenable) {
		smtp.setSpoolenable(spoolenable);
	}
	
	/** set the value server
	*  The address of the SMTP server to use for sending messages. If no server is specified, the 
	* 		server name specified in the ColdFusion Administrator is used.
	* @param strServer value to set
	 * @throws PageException 
	**/
	public void setServer(String strServer) throws PageException {
		smtp.setHost(strServer);
	}
 
	/** set the value mailerid
	*  Specifies a mailer ID to be passed in the X-Mailer SMTP header, which identifies the mailer 
	* 		application. The default is ColdFusion Application Server.
	* @param mailerid value to set
	**/
	public void setMailerid(String mailerid)	{
		smtp.setXMailer(mailerid);
	}
	
	/** set the value port
	*  The TCP/IP port on which the SMTP server listens for requests. This is normally 25.
	* @param port value to set
	**/
	public void setPort(double port)	{
		smtp.setPort((int)port);
	}
	
	/**
	 * @param wraptext The wraptext to set.
	 */
	public void setWraptext(double wraptext) {
		getPart().setWraptext((int)wraptext);
	}

	/** set the value timeout
	*  The number of seconds to wait before timing out the connection to the SMTP server.
	* @param timeout value to set
	**/
	public void setTimeout(double timeout)	{
		smtp.setTimeout((int)(timeout*1000));
	}
	
	/**
	 * @param charset The charset to set.
	 */
	public void setCharset(String charset) {
		this.charset=charset;
	}

	/** set the value group
	*  Specifies the query column to use when you group sets of records together to send as an e-mail 
	* 		message. For example, if you send a set of billing statements to customers, you might group on 
	* 		"Customer_ID." The group attribute, which is case sensitive, eliminates adjacent duplicates when the 
	* 		data is sorted by the specified field. See the Usage section for exceptions.
	* @param group value to set
	**/
	public void setGroup(String group)	{
		this.group=group;
	}

	/** set the value groupcasesensitive
	*  Boolean indicating whether to group with regard to case or not. The default value is YES; 
	* 		case is considered while grouping. If the query attribute specifies a query object that was generated 
	* 		by a case-insensitive SQL query, set the groupCaseSensitive attribute to NO to keep the recordset 
	* 		intact.
	* @param groupcasesensitive value to set
	**/
	public void setGroupcasesensitive(boolean groupcasesensitive)	{
		this.groupcasesensitive=groupcasesensitive;
	}

	/** set the value query
	*  The name of the cfquery from which to draw data for message(s) to send. Specify this 
	* 		attribute to send more than one mail message, or to send the results of a query within a message.
	* @param query value to set
	**/
	public void setQuery(String query)	{
		this.query=query;
	}

	/** set the value maxrows
	*  Specifies the maximum number of e-mail messages to send.
	* @param maxrows value to set
	**/
	public void setMaxrows(double maxrows)	{
		this.maxrows=maxrows;
	}
	

	public void setTls(boolean tls)	{
		smtp.setTLS(tls);
	}	
	
	public void setUsetls(boolean tls)	{
		smtp.setTLS(tls);
	}	
	
	public void setStarttls(boolean tls)	{
		smtp.setTLS(tls);
	}

	public void setSsl(boolean ssl)	{
		smtp.setSSL(ssl);
	}

	public void setUsessl(boolean ssl)	{
		smtp.setSSL(ssl);
	}

	public void setSecure(boolean ssl)	{
		smtp.setSSL(ssl);
	}
	public void setPriority(String strPriority) throws ExpressionException	{
		strPriority=strPriority.trim().toLowerCase();
		boolean valid=true;
		if(Decision.isNumeric(strPriority)) {
			int p=Caster.toIntValue(strPriority,-1);
			if(p<1 || p>5)valid=false;
			else this.priority=p;
		}
		else {
			if("highest".equals(strPriority))priority=1;
			else if("urgent".equals(strPriority))priority=1;
			else if("high".equals(strPriority))priority=2;
			else if("normal".equals(strPriority))priority=3;
			else if("low".equals(strPriority))priority=4;
			else if("lowest".equals(strPriority))priority=5;
			else if("non-urgent".equals(strPriority))priority=5;
			else if("none-urgent".equals(strPriority))priority=5;
			else valid=false;
		}
		
		if(!valid)throw new ExpressionException("the value of attribute priority is invalid ["+strPriority+"], " +
				"the value should be an integer between [1-5] or " +
				"one of the following [highest,urgent,high,normal,low,lowest,non-urgent]");
		
	}

	/** set the value startrow
	*  Specifies the row in the query to start from.
	* @param startrow value to set
	**/
	public void setStartrow(double startrow)	{
		this.startrow=startrow;
	}

    /**
     * @param part
     */
    public void setBodyPart(MailPart part) {
        if(part.getCharset()==null) part.setCharset(getCharset());
        addBody(part);
    }
	
	/**
     * @param part
     */
    private void addBody(MailPart part) {
        if(part.isHTML()) {
            if(!smtp.hasHTMLText())smtp.setHTMLText(part.getBody(), part.getCharset());
        }
        else {
            if(!smtp.hasPlainText())smtp.setPlainText(part.getBody(), part.getCharset());
        }
    }


    /**
	* @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
	public int doStartTag()	{
		return EVAL_BODY_BUFFERED;
	}

	/**
	* @see javax.servlet.jsp.tagext.BodyTag#doInitBody()
	*/
	public void doInitBody()	{
		
	}

	/**
	* @see javax.servlet.jsp.tagext.BodyTag#doAfterBody()
	*/
	public int doAfterBody()	{
		getPart().setBody(bodyContent.getString());
		smtp.setCharset(getCharset());
		getPart().setCharset(getCharset());
		//print.ln(getCharset());
		//print.ln(MimeUtility.javaCharset(getCharset()));
		addBody(getPart());
		return SKIP_BODY;
	}
	
	/**
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	*/
	public int doEndTag() throws PageException	{
		
		try {
			smtp.send((ConfigImpl) pageContext.getConfig());
		} 
		catch (MailException e) {
			throw Caster.toPageException(e);
		}
		return EVAL_PAGE;
	}

	/**
	 * sets a mail param
	 * @param type
	 * @param file
	 * @param name
	 * @param value
	 * @param contentID 
	 * @param disposition 
	 * @throws PageException 
	 */
	public void setParam(String type, String file, String name, String value, String disposition, String contentID) throws PageException {
		if(file!=null)setMimeattach(file,type,disposition,contentID);
		else smtp.addHeader(name,value);
	}	
	
	private railo.runtime.net.mail.MailPart getPart() {
		if(part==null)part=new railo.runtime.net.mail.MailPart(pageContext.getConfig().getMailDefaultEncoding());
		return part;
	}


	/**
	 * @return the charset
	 */
	public String getCharset() {
		if(charset==null)charset=pageContext.getConfig().getMailDefaultEncoding();
		return charset;
	}
}