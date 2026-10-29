# Funcional 1 — registros e listas

A extensão faz parte do módulo **Funcional1** do repositório PLP. Ela usa os ambientes, expressões, declarações e valores da linguagem do professor. A IDE seleciona **Func1** e executa a extensão pela API JavaScript gerada pelo TeaVM.

## Executar na IDE web

Na raiz de `PLP`, use JDK 17 ou 21, Maven e uma versão do Node compatível com o Vite 8 (20.19+ ou 22.12+). A integração foi validada com JDK 21.

```bash
cd WebUI
npm ci
npm start
```

Abra <http://localhost:4004>, clique no seletor de linguagem do notebook e escolha **Func1**. Cole um dos exemplos abaixo na célula de código e clique no botão ▶ (Run cell). Cada célula de Func1 é um programa completo: a declaração `let record ... in` deve estar na mesma célula que a usa.

`npm start` compila os módulos Java necessários, gera `plp.js`, copia o resultado para `WebUI/public` e inicia a IDE. Depois de alterar arquivos Java ou `.jj`, execute esse comando novamente. Alterações apenas em TypeScript são atualizadas pelo Vite.

No macOS com o Java 21 instalado pelo Homebrew, caso seu Java padrão seja mais recente:

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home
```

Se a pasta estiver sincronizada com iCloud ou outro serviço e surgirem `.jar` incompletos ou cópias com sufixo `2`, execute a compilação em uma cópia local fora da pasta sincronizada. A compilação automática do VS Code também pode sobrescrever `target/classes`; se aparecer `Unresolved compilation problem` durante um teste Maven, desative a compilação automática do editor durante a geração. A validação passou em uma cópia limpa com `mvn clean verify` e também na pasta original com `mvn verify`, após reconstruir os artefatos locais.

## Demonstração

Listas de registros e campos que contêm listas:

```text
let record Pessoa { nome: String, notas: [Int] } in
let var turma = [
    Pessoa { nome: "Monique", notas: [8, 10] },
    Pessoa { nome: "Bruno", notas: [9, 7] }
] in
head(head(turma).notas) + head(head(tail(turma)).notas)
```

Resultado: `17`. `head(turma)` devolve um registro; `.notas` acessa a lista armazenada nele; o próximo `head` devolve a primeira nota.

Listas aninhadas também são aceitas: `head(head([[1, 2], [3, 4]]))` resulta em `1`. Inteiros, booleanos, strings, registros e listas podem ser elementos de uma lista. Cada lista mantém tipos compatíveis entre seus elementos.

Para demonstrar um erro de tipo, execute `[1, true]`. Para demonstrar um campo com tipo incorreto:

```text
let record Pessoa { notas: [Int] } in
Pessoa { notas: [true] }
```

Ambos são rejeitados antes da avaliação. `head([])` passa pela checagem de tipos, mas falha ao executar porque a lista está vazia. Esse é um erro de execução, pois depende do conteúdo da lista.

## Gramática acrescentada e código correspondente

A tabela usa EBNF: `[ ... ]` indica uma parte opcional; `{ ... }` indica repetição; os colchetes entre aspas pertencem à sintaxe da linguagem. As alternativas abaixo são acrescentadas às expressões que a LF1 já tinha. Clique no nome de cada regra para abrir sua implementação.

| Regra | Produção acrescentada |
|---|---|
| [ExpDeclaracaoRegistro](src/lf1/plp/functional1/extension/ExpDeclaracaoRegistro.java) | `"let" "record" Id "{" [DclCampos] "}" "in" Expressao` |
| [DclCampos](src/lf1/plp/functional1/extension/TipoRegistro.java) | `Id ":" TipoCampo { "," Id ":" TipoCampo }` |
| [TipoCampo](src/lf1/plp/functional1/extension/TipoNomeado.java) | `"Int" \| "Boolean" \| "String" \| Id \| "[" TipoCampo "]"` |
| [ExpRegistro](src/lf1/plp/functional1/extension/ExpRegistro.java) | `Id "{" [Campos] "}"` |
| [Campos](src/lf1/plp/functional1/extension/ExpRegistro.java) | `Id ":" Expressao { "," Id ":" Expressao }` |
| [ExpLista](src/lf1/plp/functional1/extension/ExpLista.java) | `"[" [Expressao { "," Expressao }] "]"` |
| [ExpCampo](src/lf1/plp/functional1/extension/ExpCampo.java) | `Expressao "." Id` |
| [ExpOperacaoLista](src/lf1/plp/functional1/extension/ExpOperacaoLista.java) | `("head" \| "tail" \| "isEmpty") "(" Expressao ")"` |

`DclCampos` e `Campos` são sequências auxiliares da gramática, representadas por mapas de nomes para tipos e de nomes para expressões. Não são classes Java separadas. A produção recursiva `TipoCampo` permite `[Int]`, `[Pessoa]` e `[[Int]]`.

O parser concreto está em [Functional1.jj](src/lf1/plp/functional1/parser/Functional1.jj). Ele usa as produções `PDeclaracaoRegistro`, `PRegistro`, `PLista` e `PTipoCampo`; o acesso a campos e as operações de listas são construídos pelas produções de expressões.

A sintaxe abstrata de valores também foi ampliada:

```text
Valor ::= ValorConcreto
ValorConcreto ::= ValorInteiro | ValorBooleano | ValorString | ValorLista | ValorRegistro
```

[ValorLista](src/lf1/plp/functional1/extension/ValorLista.java) e [ValorRegistro](src/lf1/plp/functional1/extension/ValorRegistro.java) estendem a classe existente [ValorConcreto](src/lf1/plp/expressions2/expression/ValorConcreto.java), que implementa [Valor](src/lf1/plp/expressions2/expression/Valor.java). Os valores primitivos continuam participando da mesma hierarquia. Uma expressão de lista é um nó de sintaxe; ao ser avaliada, produz um valor de lista.

## Decisões e verificação de tipos

- [ExpLista](src/lf1/plp/functional1/extension/ExpLista.java) verifica as expressões dos elementos e unifica seus tipos. A lista vazia começa com um tipo de elemento ainda não determinado, que pode ser definido pelo contexto.
- [ExpRegistro](src/lf1/plp/functional1/extension/ExpRegistro.java) consulta a declaração do registro, compara os nomes dos campos e verifica o tipo de cada expressão com o tipo declarado. A ordem dos campos pode variar.
- [ExpCampo](src/lf1/plp/functional1/extension/ExpCampo.java) exige um registro e um campo existente. [ExpOperacaoLista](src/lf1/plp/functional1/extension/ExpOperacaoLista.java) exige uma lista: `head` tem o tipo do elemento; `tail` tem o tipo da lista; `isEmpty` retorna booleano.
- [TipoRegistro](src/lf1/plp/functional1/extension/TipoRegistro.java) usa identidade nominal por declaração. Dois registros declarados separadamente são tipos distintos, mesmo que tenham campos iguais.
- [Inferencia](src/lf1/plp/functional1/util/Inferencia.java), [TipoPolimorfico](src/lf1/plp/functional1/util/TipoPolimorfico.java) e [TipoFuncao](src/lf1/plp/functional1/util/TipoFuncao.java) propagam as restrições de listas e campos pelos parâmetros, retornos e chamadas de funções.
- Os valores de listas e registros são imutáveis. As novas expressões implementam `Expressao` por meio de [ExpressaoExtensao](src/lf1/plp/functional1/extension/ExpressaoExtensao.java); cada classe concreta declara sua própria regra de `checaTipo`.

`checaTipo` valida as regras; `getTipo` informa ou infere o tipo; `avaliar` produz o valor. A checagem pode retornar `false` quando uma subexpressão é inválida ou lançar um diagnóstico de tipo. O parser envolve expressões da extensão com [ExpressaoLocalizada](src/lf1/plp/functional1/extension/ExpressaoLocalizada.java), que acrescenta linha e coluna ao diagnóstico. A API da IDE chama a checagem antes de executar.

## Integração com o repositório e o navegador

O caminho de execução é:

```text
WebUI (Func1) → WebAPI.__runCode → Func1DebugParser
             → AST de Funcional1 → checaTipo → avaliar → resultado na célula
```

O parser normal e o parser do [WebDebug](../WebDebug/src/plp/debug/funcional1/parser/Functional1Debug.jj) reconhecem a mesma extensão. O WebDebug reutiliza as regras de inferência de Funcional1 e acrescenta os escopos e intervalos de código usados no painel de depuração. O interpretador clássico também usa o parser atualizado.

Edite os arquivos `.jj`, pois as classes de parser são geradas pelo Maven em `target`. Elas não devem ser copiadas para `src`.

## Testes e execução pelo terminal

A partir da raiz de `PLP`:

```bash
# Regressões da LF1 e testes de listas, registros, inferência e checagem
mvn -pl Funcional1 -am test

# Todos os testes necessários à IDE e geração do JavaScript
mvn -pl WebAPI -am clean verify

# Os 24 exemplos pelo JavaScript real usado pela IDE
node WebAPI/test/funcional1-web.cjs

# Compilação da interface
cd WebUI
npm run build
```

Para executar uma classe específica de testes na raiz:

```bash
mvn -pl Funcional1 -Dtest=ListasGenericasTest test
mvn -pl Funcional1 -Dtest=RegistrosListasTest test
mvn -pl Funcional1 -Dtest=InferenciaTest test
mvn -pl Funcional1 -Dtest=Funcional1Test test
mvn -pl Funcional1 -Dtest=ChecagemExtensaoTest test
```

As classes cobrem, respectivamente: listas de diferentes tipos e listas aninhadas; construção e acesso a registros; restrições e chamadas de funções; comportamento original de Funcional1; propagação de falhas da checagem sem avaliar expressões. Os testes de [RegistrosListasWebDebugTest](../WebDebug/test/plp/debug/funcional1/RegistrosListasWebDebugTest.java) comparam os dois parsers, verificam os exemplos inválidos, os escopos e a reutilização do parser pela IDE.

Os [exemplos](examples) incluem 16 programas válidos e 8 casos de erro. [resultados.tsv](examples/resultados.tsv) registra o resultado ou o diagnóstico esperado pela API web. O teste JavaScript usa essa tabela e também confirma que uma chamada inválida não impede a execução seguinte.

Para executar um arquivo sem a IDE, após compilar `Funcional1`:

```bash
java -cp Funcional1/target/classes lf1.plp.functional1.parser.Func1Parser Funcional1/examples/registros/campos-lista.lf1
```

A página da linguagem no site da disciplina está em [linguagem_funcional1.markdown](../docs/_pages/linguagem_funcional1.markdown). A [IDE publicada](https://cybernicky.github.io/PLP/ide/) e a [documentação da linguagem](https://cybernicky.github.io/PLP/linguagens/funcional1) são atualizadas pelo GitHub Actions a partir de `master`.
