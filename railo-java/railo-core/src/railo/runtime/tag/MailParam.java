package railo.runtime.tag;


import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.mail.EmailAttachment;

import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;
/**
* Can either attach a file or add a header to a message. It is nested within a cfmail tag. You can 
*   use more than one cfmailparam tag within a cfmail tag.
*
*
*
**/
public final class MailParam extends TagImpl {

	/** Indicates the value of the header. */
	private String value="";

	/** Attaches the specified file to the message. This attribute is mutually exclusive with the 
	** 		name attribute. */
	private String file;

	/** Specifies the name of the header. Header names are case insensitive. This attribute is mutually 
	** 		exclusive with the file attribute. */
	private String name;
	
	private String type="";
    private String disposition=null;
    private String contentID=null;

	/**
	* @see javax.servlet.jsp.tagext.Tag#release()
	*/
	public void release()	{
		super.release();
		value="";
		file=null;
		name=null;
		type="";
        disposition=null;
        contentID=null;
	}
	
	/**
	 * @param type
	 */
	public void setType(String type)	{
		type=type.toLowerCase().trim();
		
		if(type.equals("text"))type="text/plain";
		else if(type.equals("plain"))type="text/plain";
		else if(type.equals("html"))type="text/html";
		
		this.type=type;
	}

	/** set the value value
	*  Indicates the value of the header.
	* @param value value to set
	**/
	public void setValue(String value)	{
		this.value=value;
	}

	/** set the value file
	*  Attaches the specified file to the message. This attribute is mutually exclusive with the 
	* 		name attribute.
	* @param strFile value to set
	 * @throws PageException 
	**/
	public void setFile(String strFile) throws PageException	{
		Resource file=ResourceUtil.toResourceNotExisting(pageContext,strFile);
        if(file!=null) {
            if(file.exists())pageContext.getConfig().getSecurityManager().checkFileLocation(file);
            strFile=ResourceUtil.getCanonicalPathEL(file);
        } 
		this.file=strFile;
	}

	/** set the value name
	*  Specifies the name of the header. Header names are case insensitive. This attribute is mutually 
	* 		exclusive with the file attribute.
	* @param name value to set
	**/
	public void setName(String name)	{
		this.name=name;
	}

    /**
     * @param disposition The disposition to set.
     * @throws ApplicationException 
     */
    public void setDisposition(String disposition) throws ApplicationException {
        disposition=disposition.trim().toLowerCase();
        if(disposition.equals("attachment")) this.disposition=EmailAttachment.ATTACHMENT;
        else if(disposition.equals("inline"))this.disposition=EmailAttachment.INLINE;
        else 
        throw new ApplicationException("disposition must have one of the following values (attachment,inline)");
        
    }
    /**
     * @param contentID The contentID to set.
     */
    public void setContentid(String contentID) {
        this.contentID = contentID;
    }


	/**
	* @throws PageException 
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
	public int doStartTag() throws PageException	{
		// get Mail Tag
		Tag parent=getParent();
		while(parent!=null && !(parent instanceof Mail)) {
			parent=parent.getParent();
		}
		
		if(parent instanceof Mail) {
			Mail mail = (Mail)parent;
			mail.setParam(type,file,name,value,disposition,contentID);
		}
		else {
			throw new ApplicationException("Wrong Context, tag MailParam must be inside a Mail tag");	
		}
		return SKIP_BODY;
	}

	/**
	* @see javax.servlet.jsp.tagext.Tag#doEndTag()
	*/
	public int doEndTag()	{
		return EVAL_PAGE;
	}

}