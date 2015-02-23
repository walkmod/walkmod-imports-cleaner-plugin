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

	@Test
	public void testMethodParamsInJavadoc() {
		String content = "This class provides a skeletal implementation of the {@code Cache} interface to minimize the\n"
				+ " effort required to implement this interface.\n\n"
				+ " <p>\n"
				+ " To implement a cache, the programmer needs only to extend this class and provide an\n"
				+ " implementation for the {@link #get(Object)} and {@link #getIfPresent} methods.\n"
				+ " {@link #getUnchecked}, {@link #get(Object, Callable)}, and {@link #getAll} are implemented in\n"
				+ " terms of {@code get}; {@link #getAllPresent} is implemented in terms of {@code getIfPresent};\n"
				+ " {@link #putAll} is implemented in terms of {@link #put}, {@link #invalidateAll(Iterable)} is\n"
				+ " implemented in terms of {@link #invalidate}. The method {@link #cleanUp} is a no-op. All other\n"
				+ " methods throw an {@link UnsupportedOperationException}.";
		ImportsOrganizer io = new ImportsOrganizer();
		JavadocComment comment = new JavadocComment(content);
		CompilationUnit cu = new CompilationUnit();
		io.visit(cu, null);
		io.visit(comment, null);
		Assert.assertTrue(io.getReferencedTypes().contains("Callable"));
	}

	@Test
	public void testClassInJavadocs() {
		String content = "Returns a comparator that compares two arrays of unsigned {@code int} values lexicographically.\n"
				+ " That is, it compares, using {@link #compare(int, int)}), the first pair of values that follow\n"
				+ " any common prefix, or when one array is a prefix of the other, treats the shorter array as the\n"
				+ " lesser. For example, {@code [] < [1] < [1, 2] < [2] < [1 << 31]}.\n"
				+

				" <p>\n"
				+ " The returned comparator is inconsistent with {@link Object#equals(Object)} (since arrays\n"
				+ " support only identity equality), but it is consistent with {@link Arrays#equals(int[], int[])}.\n"
				+

				" @see <a href=\"http://en.wikipedia.org/wiki/Lexicographical_order\"> Lexicographical order\n"
				+ "      article at Wikipedia</a>\n";
		ImportsOrganizer io = new ImportsOrganizer();
		JavadocComment comment = new JavadocComment(content);
		CompilationUnit cu = new CompilationUnit();
		io.visit(cu, null);
		io.visit(comment, null);
		Assert.assertTrue(io.getReferencedTypes().contains("Arrays"));
	}
}
