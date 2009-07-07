package railo.runtime.tag;

import railo.runtime.exp.DeprecatedException;
import railo.runtime.ext.tag.TagImpl;

public final class TextInput extends TagImpl {

	public TextInput() throws DeprecatedException {
		throw new DeprecatedException("textinput");
	}
	
	public void setAlign(String align) {}
	public void setBgcolor(String bgColor) {}
	public void setBold(boolean bold) {}
	public void setFont(String font) {}
	public void setFontsize(double fontSize) {}
	public void setHeight(double height) {}
	public void setHspace(double space) {}
	public void setItalic(boolean italic) {}
	public void setMaxlength(double maxLength) {}
	public void setMessage(String message) {}
	public void setName(String name) {}
	public void setNotsupported(String notSupported) {}
	public void setOnerror(String onError) {}
	public void setOnvalidate(String onValidate) {}
	public void setRange(String range) {}
	public void setRequired(boolean required) {}
	public void setSize(double size) {}
	public void setTextcolor(String textColor) {}
	public void setValidate(boolean validate) {}
	public void setValue(String value) {}
	public void setVspace(double space) {}
	public void setWidth(double width) {}	
}
