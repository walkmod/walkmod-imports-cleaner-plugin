/* 
  Copyright (C) 2013 Raquel Pau and Albert Coroleu.
 
 Walkmod is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 Walkmod is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Lesser General Public License for more details.
 
 You should have received a copy of the GNU Lesser General Public License
 along with Walkmod.  If not, see <http://www.gnu.org/licenses/>.*/

package org.walkmod.importscleaner.visitors;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.walkmod.javalang.JavadocManager;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.ImportDeclaration;
import org.walkmod.javalang.ast.body.JavadocComment;
import org.walkmod.javalang.ast.body.JavadocTag;
import org.walkmod.javalang.ast.expr.NameExpr;
import org.walkmod.javalang.ast.type.ClassOrInterfaceType;
import org.walkmod.javalang.visitors.VoidVisitorAdapter;
import org.walkmod.walkers.VisitorContext;

public class ImportsOrganizer extends VoidVisitorAdapter<VisitorContext> {

	private Set<String> referencedTypes;

	private static Logger log = Logger.getLogger(ImportsOrganizer.class);

	public void visit(ClassOrInterfaceType n, VisitorContext ctx) {
		if (n.getScope() != null) {
			referencedTypes.add(n.getScope().toString() + "." + n.getName());
			referencedTypes.add(n.getScope().toString());
		} else {
			referencedTypes.add(n.getName());
		}
		super.visit(n, ctx);
	}

	public void visit(NameExpr n, VisitorContext ctx) {
		referencedTypes.add(n.toString());
		super.visit(n, ctx);
	}

	public void visit(JavadocComment n, VisitorContext ctx) {
		try {
			List<JavadocTag> tags = JavadocManager.parse(n.getContent());

			if (tags != null) {
				for (JavadocTag tag : tags) {
					String name = tag.getName();
					if ("@link".equals(name) || "@linkplain".equals(name)) {
						List<String> values = tag.getValues();
						if (values != null) {
							String type = values.get(0);
							if (type != null) {
								String typeName = type.split("#")[0];
								if(!"".equals(typeName)){
									referencedTypes.add(typeName);
								}
							}
						}
					} else if ("@see".equals(name)) {
						List<String> values = tag.getValues();
						if (values != null) {
							String type = values.get(0);
							if (type != null && !type.startsWith("<")
									&& !type.startsWith("\"")) {
								String typeName = type.split("#")[0];
								if(!"".equals(typeName)){
									referencedTypes.add(typeName);
								}
							}
						}
					}
				}
			}

		} catch (Exception e) {
			log.warn("Parsing error in the javadoc " + n.getContent());
		}

	}

	public void visit(CompilationUnit n, VisitorContext ctx) {
		referencedTypes = new HashSet<String>();
		super.visit(n, ctx);
		List<ImportDeclaration> imports = n.getImports();
		if (imports != null && !referencedTypes.isEmpty()) {
			List<ImportDeclaration> cleanImportList = new LinkedList<ImportDeclaration>(
					imports);
			Iterator<ImportDeclaration> itImport = cleanImportList.iterator();
			while (itImport.hasNext()) {
				ImportDeclaration id = itImport.next();
				if (!id.isStatic() && !id.isAsterisk()) {
					boolean found = false;
					String importName = id.getName().toString();
					Iterator<String> it = referencedTypes.iterator();
					while (it.hasNext() && !found) {
						String name = it.next();
						int dotIndex = name.indexOf(".");
						if (dotIndex != -1) {
							name = name.substring(dotIndex);
						} else {
							name = "." + name;
						}
						found = importName.endsWith(name);
					}
					if (!found) {
						itImport.remove();
						log.debug("Removed " + importName);
					}
				}
			}
			n.setImports(cleanImportList);
		}
	}
}
