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

**Acesso rápido:** [checaTipo e código](#checa-tipo) · [Gramática acrescentada](#gramatica-da-extensao) · [Exemplos válidos](#exemplos-validos) · [Erros de tipo](#erros-de-tipo) · [Erros de sintaxe](#erros-de-sintaxe) · [Erros de execução](#erros-de-execucao).

Para testar, abra a [IDE web]({{ site.baseurl }}/ide/), selecione **Func1**, cole um programa completo na célula e pressione **Shift + Enter** ou clique em **Run**. As declarações de um exemplo precisam estar na mesma célula que seu uso. Cada exemplo abaixo informa o resultado ou diagnóstico esperado.

<a id="checa-tipo"></a>
## Checagem de tipos — checaTipo visível

A checagem ocorre **antes da avaliação do programa**. Ela percorre as expressões e consulta o ambiente de compilação para verificar tipos de variáveis, funções e registros. Encontrar um tipo como Int ou Boolean, por si só, não aprova uma construção: é necessário satisfazer a regra da expressão.

| Método | Responsabilidade | Resultado |
|---|---|---|
| `checaTipo(amb)` | Verificar subexpressões e regras de compatibilidade. | `true` quando a construção é válida; falhas podem retornar `false` ou lançar `ErroExtensao`, conforme a regra atual. |
| `getTipo(amb)` | Consultar ou inferir o tipo da expressão e propagar restrições de tipos. | Um objeto `Tipo`, como Int, Boolean, Pessoa ou [Int]. |
| `avaliar(amb)` | Executar a expressão no ambiente de execução. | Um `Valor`, como 7, true, uma lista ou um registro. |

A API da IDE chama a checagem e executa o programa apenas se ela aprovar: [ver interpretarFunc1](https://github.com/{{ site.github.owner_name }}/{{ site.github.repository_name }}/blob/master/WebAPI/src/main/java/plp/web/PlpWebApi.java#L128-L139). O diagnóstico aparece na saída da célula. Alguns erros incluem linha e coluna; outros, como incompatibilidade entre ramos de `if`, usam a mensagem geral `erro de tipos!`.

### checaTipo de uma lista

[ExpLista.checaTipo](https://github.com/{{ site.github.owner_name }}/{{ site.github.repository_name }}/blob/master/Funcional1/src/lf1/plp/functional1/extension/ExpLista.java#L17-L25) — código atual da implementação:

```java
@Override public boolean checaTipo(AmbienteCompilacao amb) {
    Tipo elemento = new TipoPolimorfico();
    for (Expressao exp : elementos) {
        if (!exp.checaTipo(amb)) return false;
        if (!Inferencia.unificar(elemento, exp.getTipo(amb)))
            throw new ErroExtensao("Todos os elementos da lista devem ter o mesmo tipo.");
    }
    return true;
}
```

1. Começa com um tipo de elemento ainda não determinado (`TipoPolimorfico`).
2. Verifica cada expressão da lista com `exp.checaTipo(amb)`. Se uma delas retornar `false`, a lista também é rejeitada.
3. `Inferencia.unificar` exige tipos compatíveis para todos os elementos e propaga as restrições. Assim, `[1, 2]` é aceito e `[1, true]` produz erro.
4. Retorna `true` somente depois dessas verificações. Para `[]`, não há elementos para comparar; o contexto pode determinar o tipo posteriormente.

### checaTipo de um registro

[ExpRegistro.checaTipo](https://github.com/{{ site.github.owner_name }}/{{ site.github.repository_name }}/blob/master/Funcional1/src/lf1/plp/functional1/extension/ExpRegistro.java#L22-L39) — código atual da implementação:

```java
@Override public boolean checaTipo(AmbienteCompilacao amb) {
    TipoRegistro tipo;
    try { tipo = (TipoRegistro) amb.get(TipoRegistro.chave(nome)); }
    catch (VariavelNaoDeclaradaException e) { throw new ErroExtensao("Tipo de registro não declarado: " + nome); }
    if (!tipo.campos().keySet().equals(campos.keySet()))
        throw new ErroExtensao("Campos incorretos de " + nome + ": esperados " + tipo.campos().keySet()
            + ", recebidos " + campos.keySet());
    for (Map.Entry<String, Expressao> campo : campos.entrySet()) {
        if (!campo.getValue().checaTipo(amb)) return false;
        Tipo recebido = campo.getValue().getTipo(amb);
        Tipo esperado = tipo.campos().get(campo.getKey());
        if (!esperado.eIgual(recebido)) throw ExpressaoLocalizada.localizar(campo.getValue(),
            new ErroExtensao("Tipo incompatível no campo '" + campo.getKey() + "' de " + nome
                + ": esperado " + esperado + ", recebido " + recebido));
    }
    tipoVerificado = tipo;
    return true;
}
```

O método procura a declaração do registro no ambiente, exige os mesmos nomes de campos, verifica cada expressão recebida e compara seu tipo com o tipo declarado usando `eIgual`. Por exemplo, um campo `notas: [Int]` aceita `[8, 10]` e rejeita `[true]`. A comparação usa os nomes dos campos, permitindo informá-los em outra ordem.

### Onde ficam as outras regras

| Construção | Regra verificada | Implementação |
|---|---|---|
| Acesso `p.campo` | O alvo precisa ser um registro, com o campo solicitado. Em parâmetros de funções, a inferência registra essa restrição para conferir a chamada. | [ExpCampo.checaTipo](https://github.com/{{ site.github.owner_name }}/{{ site.github.repository_name }}/blob/master/Funcional1/src/lf1/plp/functional1/extension/ExpCampo.java) |
| `head`, `tail`, `isEmpty` | O argumento precisa ser uma lista, ou um parâmetro cujo tipo possa ser unificado com lista. | [ExpOperacaoLista.checaTipo](https://github.com/{{ site.github.owner_name }}/{{ site.github.repository_name }}/blob/master/Funcional1/src/lf1/plp/functional1/extension/ExpOperacaoLista.java) |
| `let record ... in ...` | Registra o tipo no escopo, resolve os tipos dos campos e verifica o corpo. O escopo é restaurado ao terminar. | [ExpDeclaracaoRegistro.checaTipo](https://github.com/{{ site.github.owner_name }}/{{ site.github.repository_name }}/blob/master/Funcional1/src/lf1/plp/functional1/extension/ExpDeclaracaoRegistro.java) |
| Chamada de função | Verifica os argumentos e sua compatibilidade com os parâmetros e o retorno inferidos. | [Aplicacao](https://github.com/{{ site.github.owner_name }}/{{ site.github.repository_name }}/blob/master/Funcional1/src/lf1/plp/functional1/expression/Aplicacao.java) e [DefFuncao](https://github.com/{{ site.github.owner_name }}/{{ site.github.repository_name }}/blob/master/Funcional1/src/lf1/plp/functional1/util/DefFuncao.java) |

**Tipo correto não garante ausência de erro de execução:** `head([])` e `tail([])` possuem argumento do tipo lista e passam pela checagem, mas falham na avaliação porque a lista está vazia. Já `head(10)` falha na checagem, pois seu argumento é inteiro.

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

<a id="gramatica-da-extensao"></a>
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

<a id="exemplos-validos"></a>
## Exemplos válidos

Nos exemplos aceitos, `checaTipo` aprova o programa e a IDE mostra o resultado de sua avaliação. As aspas nas saídas de texto fazem parte da representação usada pelo interpretador.

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

### Somar uma lista de inteiros com recursão

```text
let fun somar xs =
    if isEmpty(xs) then 0
    else head(xs) + somar(tail(xs))
in somar([2, 4, 6])
```

**Saída esperada:** `12`.

A função percorre a lista até encontrar a lista vazia. Cada elemento precisa ser inteiro para participar da soma.

[Abrir este exemplo no repositório](https://github.com/{{ site.github.owner_name }}/{{ site.github.repository_name }}/blob/master/Funcional1/examples/listas/somar-recursivamente.lf1).

### Combinar head e tail em uma lista de textos

```text
head(tail(["Ana", "Bia", "Cris"]))
```

**Saída esperada:** `"Bia"`.

tail devolve a lista sem o primeiro elemento; head seleciona o primeiro elemento dessa nova lista.

[Abrir este exemplo no repositório](https://github.com/{{ site.github.owner_name }}/{{ site.github.repository_name }}/blob/master/Funcional1/examples/listas/segundo-texto.lf1).

### Usar elementos booleanos em uma expressão lógica

```text
head([true, false]) and not head(tail([true, false]))
```

**Saída esperada:** `true`.

As operações de lista preservam o tipo Boolean dos elementos, permitindo usar and e not.

[Abrir este exemplo no repositório](https://github.com/{{ site.github.owner_name }}/{{ site.github.repository_name }}/blob/master/Funcional1/examples/listas/operacoes-booleanas.lf1).

### Reconhecer a lista vazia após tail

```text
isEmpty(tail([7]))
```

**Saída esperada:** `true`.

tail de uma lista com um elemento devolve []; isEmpty testa essa lista sem acessar elementos.

[Abrir este exemplo no repositório](https://github.com/{{ site.github.owner_name }}/{{ site.github.repository_name }}/blob/master/Funcional1/examples/listas/esvaziar-lista.lf1).

### Informar campos em outra ordem e usar expressões

```text
let record Pessoa { nome: String, idade: Int } in
Pessoa { idade: 20 + 3, nome: "Mo" ++ "nique" }.idade
```

**Saída esperada:** `23`.

Os campos são associados pelo nome. A ordem pode variar, e o valor de um campo pode ser uma expressão bem tipada.

[Abrir este exemplo no repositório](https://github.com/{{ site.github.owner_name }}/{{ site.github.repository_name }}/blob/master/Funcional1/examples/registros/campos-fora-de-ordem.lf1).

### Armazenar um registro dentro de outro registro

```text
let record Endereco { cidade: String } in
let record Pessoa { nome: String, endereco: Endereco } in
Pessoa {
    nome: "Monique",
    endereco: Endereco { cidade: "Recife" }
}.endereco.cidade
```

**Saída esperada:** `"Recife"`.

O campo endereco possui o tipo nominal Endereco. O acesso encadeado encontra a cidade dentro desse registro.

[Abrir este exemplo no repositório](https://github.com/{{ site.github.owner_name }}/{{ site.github.repository_name }}/blob/master/Funcional1/examples/registros/registro-aninhado.lf1).

### Declarar um campo como lista de registros

```text
let record Pessoa { nome: String, idade: Int } in
let record Turma { alunos: [Pessoa] } in
let var turma = Turma { alunos: [
    Pessoa { nome: "Monique", idade: 23 },
    Pessoa { nome: "Bruno", idade: 25 }
] } in
head(turma.alunos).nome
```

**Saída esperada:** `"Monique"`.

O tipo [Pessoa] permite uma lista de registros no campo alunos. head devolve uma Pessoa, e .nome devolve uma String.

[Abrir este exemplo no repositório](https://github.com/{{ site.github.owner_name }}/{{ site.github.repository_name }}/blob/master/Funcional1/examples/registros/turma-com-alunos.lf1).

### Reutilizar uma função com listas de tipos diferentes

```text
let fun primeiro xs = head(xs) in
if primeiro([true, false])
then primeiro([10, 20])
else 0
```

**Saída esperada:** `10`.

Cada chamada instancia o tipo da função: uma recebe [Boolean] e outra recebe [Int]. Cada lista, individualmente, continua homogênea.

[Abrir este exemplo no repositório](https://github.com/{{ site.github.owner_name }}/{{ site.github.repository_name }}/blob/master/Funcional1/examples/listas/funcao-primeiro-generica.lf1).

<a id="erros-de-tipo"></a>
## Erros de tipo — rejeitados antes da avaliação

Estes programas têm uma construção sintática reconhecida, mas violam uma regra de tipos. A checagem os rejeita antes da execução, inclusive quando a expressão inválida está em um ramo que não seria escolhido por `if`.

### Elementos incompatíveis na mesma lista

```text
[1, true]
```

**Diagnóstico esperado:** `Todos os elementos da lista devem ter o mesmo tipo.` Int e Boolean não podem ser elementos da mesma lista homogênea.

### Tipo incompatível em um campo de lista

```text
let record Pessoa { notas: [Int] } in
Pessoa { notas: [true] }
```

**Diagnóstico esperado:** tipo incompatível no campo `notas`. O campo foi declarado como [Int], mas recebeu [Boolean].

### Campo inexistente no acesso

```text
let record Pessoa { idade: Int } in
Pessoa { idade: 23 }.nome
```

**Diagnóstico esperado:** `Campo inexistente 'nome' em Pessoa`. Ter um registro como alvo não basta: o campo também precisa existir.

### Campo obrigatório ausente

```text
let record Pessoa { nome: String, idade: Int } in
Pessoa { nome: "Monique" }
```

**Diagnóstico esperado:** `Campos incorretos de Pessoa: esperados [nome, idade], recebidos [nome]`.

A construção precisa fornecer exatamente os campos declarados; idade não foi informado.

[Abrir este exemplo no repositório](https://github.com/{{ site.github.owner_name }}/{{ site.github.repository_name }}/blob/master/Funcional1/examples/erros/campo-obrigatorio-ausente.lf1).

### Campo que não pertence à declaração

```text
let record Pessoa { idade: Int } in
Pessoa { idade: 23, ativa: true }
```

**Diagnóstico esperado:** `Campos incorretos de Pessoa: esperados [idade], recebidos [idade, ativa]`.

O campo ativa não foi declarado no tipo Pessoa.

[Abrir este exemplo no repositório](https://github.com/{{ site.github.owner_name }}/{{ site.github.repository_name }}/blob/master/Funcional1/examples/erros/campo-extra.lf1).

### Construção de um registro não declarado

```text
Pessoa { idade: 23 }
```

**Diagnóstico esperado:** `Tipo de registro não declarado: Pessoa`.

A declaração let record precisa estar visível no escopo da construção.

[Abrir este exemplo no repositório](https://github.com/{{ site.github.owner_name }}/{{ site.github.repository_name }}/blob/master/Funcional1/examples/erros/tipo-registro-nao-declarado.lf1).

### Tipo de um campo sem declaração

```text
let record Pessoa { endereco: Endereco } in
1
```

**Diagnóstico esperado:** `Tipo de registro não declarado: Endereco`.

Mesmo sem construir uma Pessoa, sua declaração precisa referenciar um tipo Endereco conhecido.

[Abrir este exemplo no repositório](https://github.com/{{ site.github.owner_name }}/{{ site.github.repository_name }}/blob/master/Funcional1/examples/erros/tipo-de-campo-nao-declarado.lf1).

### Registros diferentes dentro da mesma lista

```text
let record Pessoa { idade: Int } in
let record Aluno { idade: Int } in
[Pessoa { idade: 23 }, Aluno { idade: 25 }]
```

**Diagnóstico esperado:** `Todos os elementos da lista devem ter o mesmo tipo.`.

Pessoa e Aluno são tipos nominais distintos, mesmo com campos iguais. Seus valores não formam uma lista homogênea.

[Abrir este exemplo no repositório](https://github.com/{{ site.github.owner_name }}/{{ site.github.repository_name }}/blob/master/Funcional1/examples/erros/registros-nominais-distintos.lf1).

### head aplicado a um inteiro

```text
head(10)
```

**Diagnóstico esperado:** `Operação de lista exige uma lista.`.

A existência do tipo Int não torna a operação válida: head exige um argumento cujo tipo seja lista.

[Abrir este exemplo no repositório](https://github.com/{{ site.github.owner_name }}/{{ site.github.repository_name }}/blob/master/Funcional1/examples/erros/head-nao-lista.lf1).

### tail aplicado a um booleano

```text
tail(true)
```

**Diagnóstico esperado:** `Operação de lista exige uma lista.`.

tail exige uma lista; receber um Boolean viola a regra da operação.

[Abrir este exemplo no repositório](https://github.com/{{ site.github.owner_name }}/{{ site.github.repository_name }}/blob/master/Funcional1/examples/erros/tail-nao-lista.lf1).

### isEmpty aplicado a uma String

```text
isEmpty("abc")
```

**Diagnóstico esperado:** `Operação de lista exige uma lista.`.

Na extensão, String e lista são tipos diferentes. isEmpty só aceita listas.

[Abrir este exemplo no repositório](https://github.com/{{ site.github.owner_name }}/{{ site.github.repository_name }}/blob/master/Funcional1/examples/erros/isempty-nao-lista.lf1).

### Argumento incompatível em uma chamada de função

```text
let fun primeiro xs = head(xs) in
primeiro(10)
```

**Diagnóstico esperado:** `função 'primeiro': Tipo incompatível no argumento 1: esperado [?], recebido INTEIRO`.

O corpo de primeiro exige que xs seja lista. A chamada com 10 é rejeitada antes de avaliar a função.

[Abrir este exemplo no repositório](https://github.com/{{ site.github.owner_name }}/{{ site.github.repository_name }}/blob/master/Funcional1/examples/erros/funcao-primeiro-argumento-invalido.lf1).

### Ramos de if com listas de tipos incompatíveis

```text
if true then [1, 2] else [false]
```

**Diagnóstico esperado:** `erro de tipos!`.

Os dois ramos precisam produzir tipos compatíveis, mesmo quando a condição é true. [Int] e [Boolean] são incompatíveis.

[Abrir este exemplo no repositório](https://github.com/{{ site.github.owner_name }}/{{ site.github.repository_name }}/blob/master/Funcional1/examples/erros/ramos-com-listas-incompativeis.lf1).

<a id="erros-de-sintaxe"></a>
## Erros de sintaxe — rejeitados pelo parser

Nestes casos, o parser não conclui a construção da expressão. Por isso, o programa não chega a `checaTipo` nem à avaliação.

### Nome de campo repetido na declaração

```text
let record Pessoa { idade: Int, idade: Int } in
1
```

**Diagnóstico esperado:** `Linha 1, coluna 33: campo repetido: idade`.

O parser impede duas declarações do mesmo campo no mesmo registro.

[Abrir este exemplo no repositório](https://github.com/{{ site.github.owner_name }}/{{ site.github.repository_name }}/blob/master/Funcional1/examples/erros/declaracao-campo-repetido.lf1).

### Nome de campo repetido na construção

```text
let record Pessoa { idade: Int } in
Pessoa { idade: 23, idade: 25 }
```

**Diagnóstico esperado:** `Linha 2, coluna 21: campo repetido: idade`.

O parser impede informar duas vezes um mesmo campo na construção do registro.

[Abrir este exemplo no repositório](https://github.com/{{ site.github.owner_name }}/{{ site.github.repository_name }}/blob/master/Funcional1/examples/erros/construcao-campo-repetido.lf1).

### Lista sem o colchete de fechamento

```text
[1, 2
```

**Diagnóstico esperado:** `Encountered` (início da mensagem do parser, em inglês).

A análise sintática não consegue concluir a lista. O programa não chega à checagem de tipos.

[Abrir este exemplo no repositório](https://github.com/{{ site.github.owner_name }}/{{ site.github.repository_name }}/blob/master/Funcional1/examples/erros/lista-sem-fechamento.lf1).

<a id="erros-de-execucao"></a>
## Erros de execução — tipo aceito, operação inválida para o valor

A checagem conhece os tipos, mas não calcula previamente o conteúdo de todas as listas. As operações `head` e `tail` desta extensão exigem uma lista não vazia durante a avaliação. Para evitar esses erros, teste `isEmpty` antes de acessar a lista.

### head aplicado a uma lista vazia

```text
head([])
```

**Diagnóstico esperado:** `head não pode ser aplicado a uma lista vazia.` O argumento é uma lista válida em termos de tipo, mas não existe primeiro elemento.

### tail aplicado a uma lista vazia

```text
tail([])
```

**Diagnóstico esperado:** `tail não pode ser aplicado a uma lista vazia.`.

A checagem aceita o tipo lista. A avaliação falha porque a operação tail da extensão exige uma lista não vazia.

[Abrir este exemplo no repositório](https://github.com/{{ site.github.owner_name }}/{{ site.github.repository_name }}/blob/master/Funcional1/examples/erros/tail-vazia.lf1).

### Evitar o erro com isEmpty

```text
let var numeros = [] in
if isEmpty(numeros) then 0 else head(numeros)
```

**Saída esperada:** `0`. A checagem aceita os tipos dos dois ramos, mas a avaliação executa apenas o ramo escolhido. Assim, `head(numeros)` não é avaliado quando a lista está vazia.

Os arquivos de exemplos e seus resultados são verificados nos testes de integração dos dois parsers e pela API JavaScript usada pela IDE. Consulte também o [guia de execução e testes](https://github.com/{{ site.github.owner_name }}/{{ site.github.repository_name }}/blob/master/Funcional1/README.md).
