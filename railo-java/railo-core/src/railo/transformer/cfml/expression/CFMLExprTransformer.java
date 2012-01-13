package railo.transformer.cfml.expression;

import railo.print;
import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.cfml.ExprTransformer;
import railo.transformer.cfml.evaluator.EvaluatorPool;
import railo.transformer.cfml.expression.AbstrCFMLExprTransformer.Data;
import railo.transformer.cfml.script.AbstrCFMLScriptTransformer;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.util.CFMLString;

public class CFMLExprTransformer extends AbstrCFMLScriptTransformer implements ExprTransformer {

	/**
	 * @see railo.transformer.data.cfml.ExprTransformer#transformAsString(railo.transformer.library.function.FunctionLib[], org.w3c.dom.Document, railo.transformer.util.CFMLString)
	 */
	public Expression transformAsString(EvaluatorPool ep,FunctionLib[] fld, CFMLString cfml, boolean allowLowerThan) throws TemplateException {
		return transformAsString(init(ep,fld, cfml,allowLowerThan),new String[]{" ", ">", "/>"});
	}
	
	
	/**
	 * Wird aufgerufen um aus dem ￼bergebenen CFMLString einen Ausdruck auszulesen 
	 * und diesen in ein CFXD Element zu ￼bersetzten.
	 * <br />
	 * Beispiel eines ￼bergebenen String:<br />
	 * <code>session.firstName</code> oder <code>trim(left('test'&var1,3))</code>
	 * <br />
	 * EBNF:<br />
	 * <code>spaces impOp;</code>
	 * 
	 * @param fld Array von Function Libraries, 
	 * Mithilfe dieser Function Libraries kann der Transfomer buil-in Funktionen innerhalb des CFML Codes erkennen 
	 * und validieren.
	 * @param doc XML Document des aktuellen zu erstellenden CFXD
	 * @param cfml Text der transfomiert werden soll.
	 * @return Element CFXD Element
	 * @throws TemplateException
	 */
	public Expression transform(EvaluatorPool ep,FunctionLib[] fld, CFMLString cfml) throws TemplateException {
		Data data = init(ep,fld, cfml,false);
		comments(data);
		return assignOp(data);
	}
	
}
