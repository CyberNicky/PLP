package br.ufpe.cin.lf1;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import lf1.plp.expressions2.expression.*;
import lf1.plp.functional1.Programa;
import lf1.plp.functional1.parser.Func1Parser;
import lf1.plp.functional1.parser.ParseException;
import org.junit.Test;
import static org.junit.Assert.*;

public class Funcional1Test {
    private Programa parse(String source) throws Exception {
        return new Func1Parser(new StringReader(source)).Input();
    }

    private Valor evaluate(String source) throws Exception {
        Programa programa = parse(source);
        assertTrue("Programa deve passar pela verificação de tipos", programa.checaTipo());
        return programa.executar();
    }

    @Test public void variables() throws Exception {
        assertEquals(Integer.valueOf(25), ((ValorInteiro) evaluate(new String(Files.readAllBytes(java.nio.file.Paths.get("examples/funcional1/variaveis.lf1")), java.nio.charset.StandardCharsets.UTF_8))).valor());
    }

    @Test public void originalFunctionExample() throws Exception {
        assertEquals(Integer.valueOf(14), ((ValorInteiro) evaluate(new String(Files.readAllBytes(java.nio.file.Paths.get("examples/funcional1/funcoes.lf1")), java.nio.charset.StandardCharsets.UTF_8))).valor());
    }

    @Test public void originalRecursiveFactorial() throws Exception {
        assertEquals(Integer.valueOf(120), ((ValorInteiro) evaluate(new String(Files.readAllBytes(java.nio.file.Paths.get("examples/funcional1/fatorial.lf1")), java.nio.charset.StandardCharsets.UTF_8))).valor());
    }

    @Test public void operatorsAndConditional() throws Exception {
        assertEquals(Integer.valueOf(3), ((ValorInteiro) evaluate(
                "if (not false and true) then length (\"a\" ++ \"bc\") else -1")).valor());
        assertTrue(((ValorBooleano) evaluate("false or (2 - 1 == 1)")).valor());
        assertEquals("olá", ((ValorString) evaluate("\"olá\"")).valor());
    }

    @Test public void nestedVariableScope() throws Exception {
        assertEquals(Integer.valueOf(3), ((ValorInteiro) evaluate(
                "let var x = 1 in (let var x = 2 in x) + x")).valor());
    }

    @Test public void rejectsInvalidType() throws Exception {
        assertFalse(parse("1 + true").checaTipo());
    }

    @Test public void rejectsIncompleteExpression() {
        assertThrows(ParseException.class, () -> parse("let var x = 1 in"));
    }

    @Test public void requiresEndOfInput() {
        assertThrows(ParseException.class, () -> parse("1 2"));
    }
}
