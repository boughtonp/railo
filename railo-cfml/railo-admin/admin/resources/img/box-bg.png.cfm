<cfsavecontent variable="c">iVBORw0KGgoAAAANSUhEUgAAABcAAAAfCAIAAACDG8GaAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAH5JREFUeNrsk0EKACEMA636/5/5Bp/Ro7tQCKEiLKsHEXuQgCGOrUopJUyXqOp8SgwrKrfWdklZc6OdUs7r7nks573d++o+s4jIApYfdPcfDVJSStxXDIh1X9g1ETEUB8URzsBOs0Uod3JvhYF5zZZrrQBjTnNDcLTzvOsjwABwaWsihJUTlwAAAABJRU5ErkJggg==</cfsavecontent><cfoutput><cfif getBaseTemplatePath() EQ getCurrentTemplatePath()><cfcontent type="image/png" variable="#toBinary(c)#"><cfsetting showdebugoutput="no"><cfelse>data:image/png;base64,#c#</cfif></cfoutput>