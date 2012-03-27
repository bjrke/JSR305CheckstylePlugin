package de.bjrke.checkstyle.jsr305.test;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.ParametersAreNullableByDefault;

import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;

public class ClassLevelReturnValueTestObject {

	// error, double annotation
	@ParametersAreNonnullByDefault
	@ParametersAreNullableByDefault
	class BothParameterAnnotationsClass {
		
	}
	
	// no error
	@ParametersAreNullableByDefault
	interface ParameterAnnotationsInterface {
		
		// no error
		public void setAValue(String foo);
		
		// error, redundant
		public void setAnotherValue(@Nullable String foo);
		
		// no error
		public void setYetAnotherValue(@Nonnull String foopoo);
		
	}
	
	interface NoParameterAnnotationsInterface {
		
		// error, missing
		public void setAValue(String foo);
		
		// no error
		public void setAnotherValue(@Nonnull String foo);
		
		// no error
		public void setYetAnotherValue(@Nullable String foo);
		
	}
	
	@ParametersAreNonnullByDefault
	class ParameterAnnotationsClass {
		
		// no error
		public void setAValue(String foo){}
		
		// no error
		public void setAnotherValue(@Nullable String foo){}
		
		// error, redundant
		public void setYetAnotherValue(@Nonnull String foo){}
		
	}
	
	class NoParameterAnnotationsClass {
		
		// error, missing
		public void setAValue(String foo){}
		
		// no error
		public void setAnotherValue(@Nonnull String foo){}
		
		// no error
		public void setYetAnotherValue(@Nullable String foo){}
		
	}
	
	@ParametersAreNonnullByDefault
	static class DefaultNullableParameterInheritingClass implements ParameterAnnotationsInterface {
		
		@Override
		// no error
		public void setAValue(String foo) {}

		@Override
		// no error
		public void setAnotherValue(String foo) {}

		@Override
		// no error
		public void setYetAnotherValue(String foopoo) {}
		
		// no error
		public void setFoo(String foo){}
	}

	static class NullableParameterInheritingClass implements ParameterAnnotationsInterface {
		
		@Override
		// no error
		public void setAValue(String foo) {}

		@Override
		// no error
		public void setAnotherValue(String foo) {}

		@Override
		// no error
		public void setYetAnotherValue(String foopoo) {}
		
		// error
		public void setFoo(String foo){}
	}
	
	@ReturnValuesAreNonnullByDefault
	interface ReturnValueAnnotationInterface {
		// error, redundant
		@Nonnull
		public String getAValue();

		// error, disallowed
		@ReturnValuesAreNonnullByDefault
		public String getYetAnotherValue();

		// no error
		@CheckForNull
		public String getAnotherValue();
	}

	@ReturnValuesAreNonnullByDefault
	static class ReturnValueAnnotationClass {
		// error, redundant
		@Nonnull
		public String getAValue() {
			return "";
		}

		// error, disallowed
		@ReturnValuesAreNonnullByDefault
		public String getYetAnotherValue() {
			return "";
		}

		// no error
		@CheckForNull
		public String getAnotherValue() {
			return "";
		}
	}

	interface NoReturnValueAnnotationInterface {
		// no error
		@Nonnull
		public String getAValue();

		// error, disallowed
		@ReturnValuesAreNonnullByDefault
		public String getYetAnotherValue();

		// no error
		@CheckForNull
		public String getAnotherValue();
	}

}

@ParametersAreNullableByDefault
class InheritanceTest {

	@ParametersAreNonnullByDefault
	interface Foo {
		
		// no error
		void foo(String bar);
		
		// no error
		void bar(@Nullable String foo);
	}
	
	// no error
	static class Fop implements Foo{
	
		// no error, since we're overriding @NonnullByDefault from the interface
		@Override
		public void foo(@Nullable String bar) {}

		// no error
		@Override
		public void bar(String foo) {}
		
		// error
		public void baz(String foo) {}
		
	}
	
	// no error
	@ParametersAreNullableByDefault
	static class FopToo implements Foo{
	
		// this should not be an error, we are inheriting via the @Override annotation
		@Override
		public void foo(String bar) {}
		
		// no error
		@Override
		public void bar(String baz) {}
		
		// no error
		public void baz(String foo) {}
		
	}
	
	// anonymous classes can't be annotated, but this should inherit the annotations from the superclass
	public void testAnonClasses() {
		@SuppressWarnings("unused")
		Foo foo = new Foo() {
			
			// no error
			@Override
			public void foo(String barrrrrr) {}

			// no error
			@Override
			public void bar(String foo) {}
			
			// error
			public void baz(String foo) {}
		};
	}

	
}

class EnumTest {

	@ParametersAreNonnullByDefault
	enum ParameterAnnotationsEnum {
		TEST;
		
		// no error
		public void setAValue(String foo){}
		
		// no error
		public void setAnotherValue(@Nullable String foo){}
		
		// error, redundant
		public void setYetAnotherValue(@Nonnull String foo){}
		
	}
	
	enum NoParameterAnnotationsEnum {
		TEST;
		
		// error, missing
		public void setAValue(String foo){}
		
		// no error
		public void setAnotherValue(@Nonnull String foo){}
		
		// no error
		public void setYetAnotherValue(@Nullable String foo){}
		
	}
	
	@ReturnValuesAreNonnullByDefault
	enum ReturnValueAnnotationEnum {
		TEST;
		// error, redundant
		@Nonnull
		public String getAValue(){
			return "";
		}

		// error, disallowed
		@ReturnValuesAreNonnullByDefault
		public String getYetAnotherValue(){
			return "";
		}

		// no error
		@CheckForNull
		public String getAnotherValue(){
			return "";
		}
	}

	
}
