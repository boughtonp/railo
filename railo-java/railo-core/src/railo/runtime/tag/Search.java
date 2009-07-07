package railo.runtime.tag;

import java.util.Iterator;
import java.util.Map;

import railo.commons.lang.StringUtil;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.search.SearchCollection;
import railo.runtime.search.SearchData;
import railo.runtime.search.SearchDataImpl;
import railo.runtime.search.SearchEngine;
import railo.runtime.search.SearchException;
import railo.runtime.search.lucene2.suggest.SuggestionItem;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.List;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

/**
* Executes searches against data indexed in Verity collections. Collections can be created by 
*   calling the cfcollection tag, by using the ColdFusion Administrator, or through native Verity indexing 
*   tools. Collections are populated with data either with the cfindex tag, or externally, using native 
*   Verity indexing tools. Collections must be created and populated before any searches can be executed.
*
*
*
**/
public final class Search extends TagImpl {

	private static final String[] EMPTY = new String[0];

	private static final int SUGGESTIONS_ALWAYS = Integer.MAX_VALUE;
	private static final int SUGGESTIONS_NEVER = -1;

	private static final railo.runtime.type.Collection.Key FOUND = KeyImpl.getInstance("found");
	private static final railo.runtime.type.Collection.Key SEARCHED = KeyImpl.getInstance("searched");
	private static final railo.runtime.type.Collection.Key KEYWORDS = KeyImpl.getInstance("keywords");
	private static final railo.runtime.type.Collection.Key KEYWORD_SCORE = KeyImpl.getInstance("keywordScore");
	private static final railo.runtime.type.Collection.Key TIME = KeyImpl.getInstance("time");
	
	/** Specifies the criteria type for the search. */
	private short type=SearchCollection.SEARCH_TYPE_SIMPLE;

	/** Specifies the maximum number of entries for index queries. If omitted, all rows are returned. */
	private int maxrows=-1;

	/** Specifies the criteria for the search following the syntactic rules specified by type. */
	private String criteria="";

	/** Specifies the first row number to be retrieved. Default is 1. */
	private int startrow=1;

	/** The logical collection name that is the target of the search operation or an external collection 
	** 		with fully qualified path. */
	private SearchCollection[] collections;

	/** To use the language attribute you must have the ColdFusion International Search Pack installed. */
	//private String language=null;

	/** A name for the search query. */
	private String name;

	private String[] category=EMPTY;
	private String categoryTree="";
	private String status;
	private int suggestions=SUGGESTIONS_NEVER;
	private int contextPassages=-1;
	private int contextBytes=-1;
	private String contextHighlightBegin;
	private String contextHighlightEnd;
	private String previousCriteria;

	private int spellCheckMaxLevel=10;
	private String result=null;
	
	/**
	* @see javax.servlet.jsp.tagext.Tag#release()
	*/
	public void release()	{
		super.release();
		type=SearchCollection.SEARCH_TYPE_SIMPLE;
		maxrows=-1;
		criteria="";
		startrow=1;
		collections=null;
		
		category=EMPTY;
		categoryTree="";
		status=null;
		suggestions=SUGGESTIONS_NEVER;
		contextPassages=-1;
		contextBytes=-1;
		contextHighlightBegin=null;
		contextHighlightEnd=null;
		previousCriteria=null;
		

		spellCheckMaxLevel=10;
		result=null;
		
	}

	/** set the value type
	*  Specifies the criteria type for the search.
	* @param type value to set
	 * @throws ApplicationException
	**/
	public void setType(String type) throws ApplicationException	{
		if(type==null) return;
	    type=type.toLowerCase().trim();
	    if(type.equals("simple"))this.type=SearchCollection.SEARCH_TYPE_SIMPLE;
	    else if(type.equals("explicit"))this.type=SearchCollection.SEARCH_TYPE_EXPLICIT;
	    else 
	        throw new ApplicationException("attribute type of tag search has a invalid value, valid values are [simple,explicit] now is ["+type+"]");

	}

	/** set the value maxrows
	*  Specifies the maximum number of entries for index queries. If omitted, all rows are returned.
	* @param maxrows value to set
	**/
	public void setMaxrows(double maxrows)	{
		this.maxrows=(int) maxrows;
	}

	/** set the value criteria
	*  Specifies the criteria for the search following the syntactic rules specified by type.
	* @param criteria value to set
	**/
	public void setCriteria(String criteria)	{
		this.criteria=criteria;
	}

	/** set the value startrow
	*  Specifies the first row number to be retrieved. Default is 1.
	* @param startrow value to set
	**/
	public void setStartrow(double startrow)	{
		this.startrow=(int) startrow;
	}

	/** set the value collection
	*  The logical collection name that is the target of the search operation or an external collection 
	* 		with fully qualified path.
	* @param collection value to set
	 * @throws PageException
	**/
	public void setCollection(String collection) throws PageException	{
		String[] collNames=List.toStringArrayTrim(List.listToArrayRemoveEmpty(collection,','));
	    collections=new SearchCollection[collNames.length];
	    SearchEngine se = pageContext.getConfig().getSearchEngine();
	    try { 
		    for(int i=0;i<collections.length;i++) {
		        collections[i]=se.getCollectionByName(collNames[i]);
		    }
	    } catch (SearchException e) {
	        collections=null;
	        throw Caster.toPageException(e);
	    }
	}

	/** set the value language
	*  To use the language attribute you must have the ColdFusion International Search Pack installed.
	* @param language value to set
	**/
	public void setLanguage(String language)	{
		//this.language=language;
		// deprecated and ignored
	}

	/** set the value external
	*  Yes or No. Yes indicates that the collection you are searching was created outside of ColdFusion 
	* 		using native Verity indexing tools. The default is No.
	* @param external value to set
	 * @throws ApplicationException
	**/
	public void setExternal(boolean external) throws ApplicationException	{
		throw new ApplicationException("attribute external ["+external+"] in tag search is deprecated");
	}

	/** set the value name
	*  A name for the search query.
	* @param name value to set
	**/
	public void setName(String name)	{
		this.name=name;
	}  
	
	

	/**
	 * @param category the category to set
	 * @throws ApplicationException 
	 */
	public void setCategory(String listCategories)  {
		if(StringUtil.isEmpty(listCategories)) return;
		this.category = List.trimItems(List.listToStringArray(listCategories, ','));
	}


	/**
	 * @param categoryTree the categoryTree to set
	 * @throws ApplicationException 
	 */
	public void setCategorytree(String categoryTree) {
		if(StringUtil.isEmpty(categoryTree)) return;
		categoryTree=categoryTree.replace('\\', '/').trim();
		if(StringUtil.startsWith(categoryTree, '/'))categoryTree=categoryTree.substring(1);
		if(!StringUtil.endsWith(categoryTree, '/') && categoryTree.length()>0)categoryTree+="/";
		this.categoryTree = categoryTree;
	}

	/**
	 * @param contextBytes the contextBytes to set
	 * @throws ApplicationException 
	 */
	public void setContextbytes(double contextBytes) throws ApplicationException {
		this.contextBytes = (int)contextBytes;
		throw new ApplicationException("attribute contextBytes for tag search is not supported yet");
		// TODO impl tag attribute
	}

	/**
	 * @param contextHighlightBegin the contextHighlightBegin to set
	 * @throws ApplicationException 
	 */
	public void setContexthighlightbegin(String contextHighlightBegin) {
		this.contextHighlightBegin = contextHighlightBegin;
		// TODO impl tag attribute
	}

	/**
	 * @param contextHighlightEnd the contextHighlightEnd to set
	 * @throws ApplicationException 
	 */
	public void setContexthighlightend(String contextHighlightEnd) {
		this.contextHighlightEnd = contextHighlightEnd;
		// TODO impl tag attribute
	}

	/**
	 * @param contextPassages the contextPassages to set
	 * @throws ApplicationException 
	 */
	public void setContextpassages(double contextPassages) throws ApplicationException {
		this.contextPassages = (int)contextPassages;
		throw new ApplicationException("attribute contextPassages for tag search is not supported yet");
		// TODO impl tag attribute
	}
	
	
	

	/**
	 * @param previousCriteria the previousCriteria to set
	 * @throws ApplicationException 
	 */
	public void setPreviouscriteria(String previousCriteria) throws ApplicationException {
		this.previousCriteria = previousCriteria;
		throw new ApplicationException("attribute previousCriteria for tag search is not supported yet");
		// TODO impl tag attribute
	}

	/**
	 * @param status the status to set
	 * @throws ApplicationException 
	 */
	public void setStatus(String status) {
		if(!StringUtil.isEmpty(status))this.status = status;
	}

	/**
	 * @param suggestions the suggestions to set
	 * @throws ApplicationException 
	 */
	public void setSuggestions(String suggestions) throws PageException {
		if(StringUtil.isEmpty(suggestions))return;
		suggestions = suggestions.trim().toLowerCase();
		if("always".equals(suggestions)) this.suggestions=SUGGESTIONS_ALWAYS;
		else if("never".equals(suggestions)) this.suggestions=SUGGESTIONS_NEVER;
		else if(Decision.isNumeric(suggestions)) {
			this.suggestions=Caster.toIntValue(suggestions);
		}
		else	
			throw new ApplicationException("attribute suggestions has a invalid value ["+suggestions+"], valid values are [always,never,<positive numeric value>]");
		
		
	}

	/**
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
	public int doStartTag() throws PageException	{
	    //SerialNumber sn = pageContext.getConfig().getSerialNumber();
	    //if(sn.getVersion()==SerialNumber.VERSION_COMMUNITY)
	    //    throw new SecurityException("no access to this functionality with the "+sn.getStringVersion()+" version of railo");
        final String v="VARCHAR",d="DOUBLE";
        String[] cols = new String[]{"title","url","summary","score","recordssearched","key","custom1","custom2","custom3","custom4",
        							"categoryTree","category","context","size","rank","author","type","collection"};
        
        // TODO support context
        String[] types = new String[]{v,v,v,d,d,v,v,v,v,v,v,v,v,d,d,v,v,v};
        SearchData data=new SearchDataImpl(suggestions);
        
        QueryImpl qry=new QueryImpl(cols,types,0,"query");
	    	
	    SearchCollection collection;
	    long time=System.currentTimeMillis();
	    try {
		    for(int i=0;i<collections.length;i++) {
		    	collection=collections[i];
		    	startrow=collection.search(data,qry,criteria,collection.getLanguage(),type,startrow,maxrows,categoryTree,category); 
		        
		        if(maxrows>=0 && qry.getRecordcount()>=maxrows) break;
		    }
		    pageContext.setVariable(name,qry);
	    }
	    catch(SearchException se) {
	        throw Caster.toPageException(se);
	    }
	    
	    time=System.currentTimeMillis()-time;
	    Double recSearched=new Double(data.getRecordsSearched());
	    int len=qry.getRecordcount();
	    for(int i=1;i<=len;i++) {
	        qry.setAt("recordssearched",i,recSearched);
	    }
	    
	    // status
	    if(status!=null) {
	    	Struct sct=new StructImpl();
	    	pageContext.setVariable(status, sct);
	    	sct.set(FOUND, new Double(qry.getRecordcount()));
	    	sct.set(SEARCHED, recSearched);
	    	sct.set(TIME, new Double(time));
	    	
	    	// TODO impl this values
	    	
	    	Map s = data.getSuggestion();
	    	if(s.size()>0) {
	    		String key;
	    		SuggestionItem item;
	    		Iterator it = s.keySet().iterator();
	    		Struct keywords=new StructImpl();
	    		Struct keywordScore=new StructImpl();
	    		sct.set(KEYWORDS, keywords);
		    	sct.set(KEYWORD_SCORE, keywordScore);
	    		while(it.hasNext()) {
	    			key=(String) it.next();
	    			item=(SuggestionItem) s.get(key);
	    			keywords.set(key, item.getKeywords());
	    			keywordScore.set(key, item.getKeywordScore());
	    		}
	    		
	    		String query = data.getSuggestionQuery();
	    		if(query!=null) {
	    			String html = StringUtil.replace(query, "<suggestion>", "<b>", false);
	    			html = StringUtil.replace(html, "</suggestion>", "</b>", false);
		    		sct.set("suggestedQueryHTML", html);
		    		
	    			String plain = StringUtil.replace(query, "<suggestion>", "", false);
	    			plain = StringUtil.replace(plain, "</suggestion>", "", false);
		    		sct.set("suggestedQuery", plain);
	    		}
	    		
	    		
	    	}
	    	
	    	//if(suggestions!=SUGGESTIONS_NEVER)sct.set("suggestedQuery", "");
	    	//sct.set("keywords", "");
	    	//sct.set("keywordScore", "");
	    	
	    	
	    }
	    
	    
		return SKIP_BODY;
	}

	/**
	* @see javax.servlet.jsp.tagext.Tag#doEndTag()
	*/
	public int doEndTag()	{
		return EVAL_PAGE;
	}

	/**
	 * @param spellCheckMaxLevel the spellCheckMaxLevel to set
	 */
	public void setSpellcheckmaxlevel(double spellCheckMaxLevel) {
		this.spellCheckMaxLevel = (int) spellCheckMaxLevel;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(String result) {
		this.result = result;
	}

}