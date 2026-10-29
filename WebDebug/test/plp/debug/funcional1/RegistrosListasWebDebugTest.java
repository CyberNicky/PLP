package plp.debug.funcional1;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import lf1.plp.expressions2.memory.ContextoCompilacao;
import lf1.plp.functional1.Programa;
import lf1.plp.functional1.parser.Func1Parser;
import org.junit.Test;
import plp.debug.funcional1.parser.Func1DebugParser;
import static org.junit.Assert.*;

public class RegistrosListasWebDebugTest {
    @Test public void exemplosValidosProduzemMesmoResultadoNosDoisParsers() throws Exception {
        for (String linha : Files.readAllLines(Paths.get("../Funcional1/examples/resultados.tsv"), StandardCharsets.UTF_8)) {
            if (linha.startsWith("#") || linha.trim().isEmpty()) continue;
            String[] dados = linha.split("\t", 3);
            if (!dados[1].equals("0")) continue;
            String fonte = new String(Files.readAllBytes(Paths.get("../Funcional1/examples", dados[0])), StandardCharsets.UTF_8);
            Programa normal = new Func1Parser(new StringReader(fonte)).Input();
            Programa debug = new Func1DebugParser(new StringReader(fonte)).Input();
            AmbienteCompilacaoDebug amb = new AmbienteCompilacaoDebug(new ContextoCompilacao());
            assertTrue(dados[0], normal.checaTipo());
            assertTrue(dados[0], debug.getExpressao().checaTipo(amb));
            assertEquals(dados[0], dados[2], debug.executar().toString());
            assertEquals(dados[0], normal.executar().toString(), debug.executar().toString());
        }
    }
    @Test public void parserDaWebRejeitaTodosOsExemplosInvalidos() throws Exception {
        for (String linha : Files.readAllLines(Paths.get("../Funcional1/examples/resultados.tsv"), StandardCharsets.UTF_8)) {
            if (linha.startsWith("#") || linha.trim().isEmpty()) continue;
            String[] dados = linha.split("\t", 3);
            if (!dados[1].equals("1")) continue;
            String fonte = new String(Files.readAllBytes(Paths.get("../Funcional1/examples", dados[0])), StandardCharsets.UTF_8);
            try {
                Programa programa = new Func1DebugParser(new StringReader(fonte)).Input();
                if (!programa.getExpressao().checaTipo(new AmbienteCompilacaoDebug(new ContextoCompilacao()))) continue;
                programa.executar();
                fail("Programa inválido aceito: " + dados[0]);
            } catch (plp.debug.funcional1.parser.ParseException | lf1.plp.functional1.extension.ErroExtensao esperado) {
                assertFalse(esperado.getMessage().isEmpty());
            }
        }
    }
    @Test public void publicaEscoposDeRegistroVariavelEFuncaoComFaixaDeCodigo() throws Exception {
        String fonte = "let record P { notas: [Int] } in\nlet var p = P { notas: [7] } in\nlet fun primeira x = head(x.notas) in primeira(p)";
        Programa programa = new Func1DebugParser(new StringReader(fonte)).Input();
        AmbienteCompilacaoDebug amb = new AmbienteCompilacaoDebug(new ContextoCompilacao());
        assertTrue(programa.getExpressao().checaTipo(amb));
        assertEquals("7", programa.executar().toString());
        List<Map<String, Object>> frames = amb.getRecorder().getSnapshot();
        assertTrue(frames.toString(), frames.size() >= 3);
        assertTrue(frames.toString(), frames.stream().anyMatch(f -> "record".equals(f.get("scope")) && "P".equals(f.get("name"))));
        assertTrue(frames.toString(), frames.stream().anyMatch(f -> f.get("bindings").toString().contains("notas")));
        for (Map<String, Object> frame : frames) assertNotNull(frame.get("sourceRange"));
    }
    @Test public void reinitNaoVazaTiposEntreExecucoes() throws Exception {
        Func1DebugParser parser = new Func1DebugParser(new StringReader("let record P { x: Int } in P { x: 1 }.x"));
        Programa primeiro = parser.Input();
        assertTrue(primeiro.checaTipo());
        assertEquals("1", primeiro.executar().toString());
        parser.ReInit(new StringReader("let record P { x: Boolean } in P { x: true }.x"));
        Programa segundo = parser.Input();
        assertTrue(segundo.checaTipo());
        assertEquals("true", segundo.executar().toString());
    }
}
