package org.walkmod.importscleaner.visitors;

import junit.framework.Assert;

import org.junit.Test;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.body.JavadocComment;

public class ImportsOrganizerTest {

	@Test
	public void testThrowsInJavadoc() {
		ImportsOrganizer io = new ImportsOrganizer();
		JavadocComment comment = new JavadocComment(
				" Returns an ordering that compares objects according to the order "
						+ "in which they appear in the\n"
						+ "given list. Only objects present in the list (according to {@link Object#equals}) may be\n"
						+ "compared. This comparator imposes a \"partial ordering\" over the type {@code T}. Subsequent\n"
						+ "changes to the {@code valuesInOrder} list will have no effect on the returned comparator. Null\n"
						+ "values in the list are not supported.\n\n"
						+ "<p>\n"
						+ "The returned comparator throws an {@link ClassCastException} when it receives an input\n"
						+ "parameter that isn't among the provided values.\n\n"
						+ "<p>\n"
						+ "The generated comparator is serializable if all the provided values are serializable.\n"
						+

						" @param valuesInOrder the values that the returned comparator will be able to compare, in the\n"
						+ "order the comparator should induce\n"
						+ " @return the comparator described above\n"
						+ " @throws NullPointerException if any of the provided values is null\n"
						+ " @throws IllegalArgumentException if {@code valuesInOrder} contains any duplicate values\n"
						+ " (according to {@link Object#equals})\n");
		CompilationUnit cu = new CompilationUnit();
		io.visit(cu, null);
		io.visit(comment, null);
		Assert.assertTrue(io.getReferencedTypes().contains(
				"IllegalArgumentException"));
	}
}
