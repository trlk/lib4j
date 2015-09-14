package libj.jndi;

import java.io.PrintStream;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import libj.utils.Text;

public class JNDITree {

	private Context context;

	public JNDITree(String name) throws NamingException {

		context = (Context) new InitialContext().lookup(name);
	}

	public void print(PrintStream stream) {

		stream.println("root = " + context.getClass().getName());

		print(stream, context, 1);
	}

	private void print(PrintStream stream, Context ctx, int indent) {

		try {

			NamingEnumeration<Binding> en = ctx.listBindings("");

			while (en.hasMore()) {

				Binding b = en.next();

				stream.printf("%s%s = %s", Text.fill("\t", indent), b.getName(), b.getClassName());

				if (b.getObject() instanceof Context) {
					print(stream, (Context) b.getObject(), ++indent);
				}
			}

		} catch (Exception e) {
			stream.println(e.toString());
		}
	}

}
