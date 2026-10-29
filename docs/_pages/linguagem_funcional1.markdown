---
layout: linguagem
permalink: /linguagens/funcional1
linguagem: Funcional 1
short: Func1
order: 3
---

- Estende a Linguagem de Expressões 2 com funções parametrizadas e recursivas
- O corpo de uma função é uma expressão e a aplicação da função a um argumento retorna um valor
- O contexto inclui dois componentes:
    - mapeamento de identificadores em valores
    - mapeamento de identificadores (nomes de função) em definições de função
- Um programa é uma expressão
- A extensão inclui registros, listas homogêneas de qualquer tipo de dado, listas aninhadas e campos de registro com listas.

## Fontes

{% include code-link.html path="Funcional1/src/lf1/plp/functional1/Programa.java" %} ::= {% include code-link.html path="Funcional1/src/lf1/plp/expressions2/expression/Expressao.java" %}

Expressao ::= {% include code-link.html path="Funcional1/src/lf1/plp/expressions2/expression/Valor.java" %}\
{% include code-indent.html %}| {% include code-link.html path="Funcional1/src/lf1/plp/expressions2/expression/ExpUnaria.java" %}\
{% include code-indent.html %}| {% include code-link.html path="Funcional1/src/lf1/plp/expressions2/expression/ExpBinaria.java" %}\
{% include code-indent.html %}| {% include code-link.html path="Funcional1/src/lf1/plp/functional1/expression/ExpDeclaracao.java" %}\
{% include code-indent.html %}| {% include code-link.html path="Funcional1/src/lf1/plp/expressions2/expression/Id.java" %}\
{% include code-indent.html %}| {% include code-link.html path="Funcional1/src/lf1/plp/functional1/expression/Aplicacao.java" %}\
{% include code-indent.html %}| {% include code-link.html path="Funcional1/src/lf1/plp/functional1/expression/IfThenElse.java" %}\
{% include code-indent.html %}| {% include code-link.html path="Funcional1/src/lf1/plp/functional1/extension/ExpDeclaracaoRegistro.java" %}\
{% include code-indent.html %}| {% include code-link.html path="Funcional1/src/lf1/plp/functional1/extension/ExpRegistro.java" %}\
{% include code-indent.html %}| {% include code-link.html path="Funcional1/src/lf1/plp/functional1/extension/ExpLista.java" %}\
{% include code-indent.html %}| {% include code-link.html path="Funcional1/src/lf1/plp/functional1/extension/ExpCampo.java" %}\
{% include code-indent.html %}| {% include code-link.html path="Funcional1/src/lf1/plp/functional1/extension/ExpOperacaoLista.java" %}

Valor ::= {% include code-link.html path="Funcional1/src/lf1/plp/expressions2/expression/ValorConcreto.java" %}

ValorConcreto ::= {% include code-link.html path="Funcional1/src/lf1/plp/expressions2/expression/ValorInteiro.java" %}\
{% include code-indent.html %}| {% include code-link.html path="Funcional1/src/lf1/plp/expressions2/expression/ValorBooleano.java" %}\
{% include code-indent.html %}| {% include code-link.html path="Funcional1/src/lf1/plp/expressions2/expression/ValorString.java" %}\
{% include code-indent.html %}| {% include code-link.html path="Funcional1/src/lf1/plp/functional1/extension/ValorLista.java" %}\
{% include code-indent.html %}| {% include code-link.html path="Funcional1/src/lf1/plp/functional1/extension/ValorRegistro.java" %}

ExpUnaria ::= {% include code-link.html path="Funcional1/src/lf1/plp/expressions2/expression/ExpMenos.java" content='"-" Expressao' %}  
{% include code-indent.html %}| {% include code-link.html path="Funcional1/src/lf1/plp/expressions2/expression/ExpNot.java" content='"not" Expressao' %}  
{% include code-indent.html %}| {% include code-link.html path="Funcional1/src/lf1/plp/expressions2/expression/ExpLength.java" content='"length" Expressao' %}

ExpBinaria ::= {% include code-link.html path="Funcional1/src/lf1/plp/expressions2/expression/ExpSoma.java" content='Expressao "+" Expressao' %}  
{% include code-indent.html %}| {% include code-link.html path="Funcional1/src/lf1/plp/expressions2/expression/ExpSub.java" content='Expressao "-" Expressao' %}  
{% include code-indent.html %}| {% include code-link.html path="Funcional1/src/lf1/plp/expressions2/expression/ExpAnd.java" content='Expressao "and" Expressao' %}  
{% include code-indent.html %}| {% include code-link.html path="Funcional1/src/lf1/plp/expressions2/expression/ExpOr.java" content='Expressao "or" Expressao' %}  
{% include code-indent.html %}| {% include code-link.html path="Funcional1/src/lf1/plp/expressions2/expression/ExpEquals.java" content='Expressao "==" Expressao' %}  
{% include code-indent.html %}| {% include code-link.html path="Funcional1/src/lf1/plp/expressions2/expression/ExpConcat.java" content='Expressao "++" Expressao' %}

ExpDeclaracao ::= "let" {% include code-link.html path="Funcional1/src/lf1/plp/functional1/declaration/DeclaracaoFuncional.java" %} "in" Expressao

DeclaracaoFuncional ::= {% include code-link.html path="Funcional1/src/lf1/plp/functional1/declaration/DecVariavel.java" %}  
{% include code-indent.html %} | {% include code-link.html path="Funcional1/src/lf1/plp/functional1/declaration/DecFuncao.java" %}  
{% include code-indent.html %} | {% include code-link.html path="Funcional1/src/lf1/plp/functional1/declaration/DecComposta.java" %}

DecVariavel ::= "var" Id "=" Expressao

DecFuncao ::= "fun" ListId "=" Expressao

DecComposta ::= DeclaracaoFuncional "," DeclaracaoFuncional

ListId ::= Id  \|  Id ListId

Aplicacao ::=  Id"(" ListExp ")"

ListExp ::= Expressao \| Expressao, ListExp

IfThenElse ::= "if" Expressao "then" Expressao "else" Expressao


## Classes Auxiliares

{% include code-link.html path="Funcional1/src/lf1/plp/functional1/util/DefFuncao.java" %}

{% include code-link.html path="Funcional1/src/lf1/plp/expressions1/util/Tipo.java" %}\
{% include code-link.html path="Funcional1/src/lf1/plp/functional1/util/TipoFuncao.java" %}\
{% include code-link.html path="Funcional1/src/lf1/plp/functional1/util/TipoPolimorfico.java" %}

{% include code-link.html path="Funcional1/src/lf1/plp/expressions2/memory/AmbienteCompilacao.java" %}\
{% include code-link.html path="Funcional1/src/lf1/plp/expressions2/memory/ContextoCompilacao.java" %}

{% include code-link.html path="Funcional1/src/lf1/plp/functional1/memory/AmbienteFuncional.java" %}\
{% include code-link.html path="Funcional1/src/lf1/plp/functional1/memory/AmbienteExecucaoFuncional.java" %}\
{% include code-link.html path="Funcional1/src/lf1/plp/functional1/memory/ContextoFuncional.java" %}\
{% include code-link.html path="Funcional1/src/lf1/plp/functional1/memory/ContextoExecucaoFuncional.java" %}

## Exemplos de Programas

{% include code-link.html path="Funcional1/examples/funcional1/fatorial.lf1" %}

## Parser

{% include code-link.html path="Funcional1/src/lf1/plp/functional1/parser/Functional1.jj" %}

## Registros e listas — gramática acrescentada

A extensão é executada na IDE selecionando **Func1**. As regras abaixo usam EBNF: partes opcionais aparecem entre colchetes; repetições, entre chaves; símbolos entre aspas são terminais. Cada nome liga à implementação correspondente.

{% include code-link.html path="Funcional1/src/lf1/plp/functional1/extension/ExpDeclaracaoRegistro.java" content="ExpDeclaracaoRegistro" %} ::= `"let" "record" Id "{" [DclCampos] "}" "in" Expressao`

{% include code-link.html path="Funcional1/src/lf1/plp/functional1/extension/TipoRegistro.java" content="DclCampos" %} ::= `Id ":" TipoCampo { "," Id ":" TipoCampo }`

{% include code-link.html path="Funcional1/src/lf1/plp/functional1/extension/TipoNomeado.java" content="TipoCampo" %} ::= `"Int" | "Boolean" | "String" | Id | "[" TipoCampo "]"`

{% include code-link.html path="Funcional1/src/lf1/plp/functional1/extension/ExpRegistro.java" content="ExpRegistro" %} ::= `Id "{" [Campos] "}"`

{% include code-link.html path="Funcional1/src/lf1/plp/functional1/extension/ExpRegistro.java" content="Campos" %} ::= `Id ":" Expressao { "," Id ":" Expressao }`

{% include code-link.html path="Funcional1/src/lf1/plp/functional1/extension/ExpLista.java" content="ExpLista" %} ::= `"[" [Expressao { "," Expressao }] "]"`

{% include code-link.html path="Funcional1/src/lf1/plp/functional1/extension/ExpCampo.java" content="ExpCampo" %} ::= `Expressao "." Id`

{% include code-link.html path="Funcional1/src/lf1/plp/functional1/extension/ExpOperacaoLista.java" content="ExpOperacaoLista" %} ::= `("head" | "tail" | "isEmpty") "(" Expressao ")"`

`DclCampos` é definido acima e produz o mapa de nomes para tipos de `TipoRegistro`. `Campos` produz o mapa de nomes para expressões de `ExpRegistro`. Essas regras auxiliares não correspondem a classes próprias. `TipoCampo` é recursivo: aceita, por exemplo, `[Int]`, `[Pessoa]` e `[[Int]]`.

A gramática abstrata de valores inclui `ValorInteiro`, `ValorBooleano`, `ValorString`, `ValorLista` e `ValorRegistro`, todos na hierarquia `ValorConcreto` / `Valor`. Expressões constroem os valores durante a avaliação.

## Decisões da extensão

Uma lista aceita qualquer tipo de dado da linguagem, mantendo elementos de tipos compatíveis. Um campo de registro pode ter um tipo primitivo, um registro ou uma lista. Registros têm identidade nominal por declaração; listas e registros são valores imutáveis.

`checaTipo` verifica as expressões e a compatibilidade dos tipos antes de executar. `ExpLista` unifica os tipos dos elementos; `ExpRegistro` verifica os campos declarados e recebidos. Acesso a campo exige um registro, e `head`, `tail` e `isEmpty` exigem listas. `head([])` e `tail([])` são erros de execução: a lista vazia possui tipo válido, mas não possui um primeiro elemento ou uma cauda.

A inferência de funções propaga as restrições de campos e elementos de listas. O parser do WebDebug usa as mesmas classes de Funcional1 e acrescenta informações de escopo para a IDE. O guia completo e os comandos de teste estão no `Funcional1/README.md` do repositório.

## Exemplos da extensão

### Lista de registros com campos que contêm listas

```text
let record Pessoa { nome: String, notas: [Int] } in
let var turma = [
    Pessoa { nome: "Monique", notas: [8, 10] },
    Pessoa { nome: "Bruno", notas: [9, 7] }
] in
head(head(turma).notas) + head(head(tail(turma)).notas)
```

**Saída esperada:** `17`.

### Lista aninhada

```text
head(head([[1, 2], [3, 4]]))
```

**Saída esperada:** `1`.

### Função que recebe lista de registros

```text
let record Pessoa { idade: Int } in
let fun somar xs =
    if isEmpty(xs) then 0
    else head(xs).idade + somar(tail(xs))
in somar([Pessoa { idade: 23 }, Pessoa { idade: 25 }])
```

**Saída esperada:** `48`.

### Erro de tipo nos elementos

```text
[1, true]
```

**Diagnóstico esperado:** `Todos os elementos da lista devem ter o mesmo tipo.` A mensagem também informa a posição do erro.

### Erro de tipo em campo

```text
let record Pessoa { notas: [Int] } in
Pessoa { notas: [true] }
```

**Diagnóstico esperado:** tipo incompatível no campo `notas`. O programa é rejeitado antes da avaliação.
