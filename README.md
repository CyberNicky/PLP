# PLP — Registros e Listas na Funcional 1

Extensão de Funcional1 integrada ao [projeto base do professor](https://github.com/AugustoSampaio/PLP), com registros, listas genéricas e execução na IDE web.

- [IDE web](https://cybernicky.github.io/PLP/ide/) — selecione **Func1**.
- [Gramática e exemplos](https://cybernicky.github.io/PLP/linguagens/funcional1).
- [Guia de execução e testes](Funcional1/README.md).

Baseado no repositório da disciplina Paradigmas de Linguagens de Programação ([in1007](https://www.cin.ufpe.br/~in1007/)) da pós-graduação em Ciência da Computação da UFPE, ministrada pelo Prof.Dr. Augusto Sampaio.

## Estrutura

| Diretório | Descrição |
|-----------|-----------|
| `Expressoes1`, `Expressoes2`, `Funcional1-3`, `Imperativa1-2`, `Objetos1-2` | Interpretadores das linguagens, em Java |
| `Testes` | Programas de exemplo de cada linguagem |
| `AppUI` | Interface clássica (Swing e versão web via TeaVM) |
| `WebAPI` | Ponto de entrada único das linguagens, compilado para JavaScript com TeaVM (`plp.js`) |
| `WebUI` | A nova IDE web (notebooks, execução e depuração no navegador) |
| `docs` | Página da disciplina (Jekyll/GitHub Pages) |

Ambas as interfaces são publicadas automaticamente pelo workflow
[`docs.yml`](.github/workflows/docs.yml):

* IDE web: <https://cybernicky.github.io/PLP/ide/>
* Interpretador clássico: <https://cybernicky.github.io/PLP/interpretador.html>

## Execução

* Local

```bash
cd <Linguagem>
mvn clean generate-sources compile exec:java
```

* Interface clássica (AppUI)

```bash
mvn package
# Jar será gerado em ./AppUI/target/ e o JS (TeaVM) em ./AppUI/target/javascript/
```

* WebAPI

```bash
mvn package
# JS será gerado em ./WebAPI/target/javascript/
```

* IDE web (WebUI)

```bash
cd WebUI
npm install
npm start
# compila o WebAPI com TeaVM e sobe a IDE em http://localhost:4004
```

## Extensão de Funcional1: registros e listas

O módulo `Funcional1` inclui listas genéricas, registros e campos com tipos compostos, com execução na IDE web ao selecionar **Func1**. O [guia da extensão](Funcional1/README.md) reúne a gramática clicável, decisões de implementação, exemplos e comandos de testes.

## Contribuições

* Unificar os projetos das linguagens e Applet no mesmo repositório - ([commit](https://github.com/AugustoSampaio/PLP/commit/5facfa1b4017536cd25730bcece9fbd94a49aa48/))
* Refactoring para adicionar o ambiente na LE1 - ([commit](https://github.com/AugustoSampaio/PLP/commit/244d8d01e036bcbec0acccf337e09c19d6ec434c/))
* Correção de problemas relativos à quantidade de argumentos em funções nas LFs - ([commit](https://github.com/AugustoSampaio/PLP/commit/14664755276b35d0aca704f7c1da8af09ea38081/))
* Separar ContextoExecucaoFuncional entre ContextoExecucao[Valor] e ContextoFuncional[DefFuncao], na LF1 - ([commit](https://github.com/AugustoSampaio/PLP/commit/a11a34df27dcdfa444c7a25f962c67f9d1480635/))
* Geração/atualização automática do Applet através do Maven - ([commit](https://github.com/AugustoSampaio/PLP/commit/bfdf84077162698587bcbcb24cc2b9d7c987b6bc/),
[commit](https://github.com/AugustoSampaio/PLP/commit/5facfa1b4017536cd25730bcece9fbd94a49aa48/))
* Refactoring na LE2 e LFs: aplicar o padrão composite na avaliação de Declarações - ([refactors](https://github.com/AugustoSampaio/PLP/commit/bab29438f5ddafa5662073ea08e166c1e04e49b9/),  [LF3](https://github.com/AugustoSampaio/PLP/commit/c60d5f92e792b89105b242163f5bd0f1409f72b4/),  [LF2](https://github.com/AugustoSampaio/PLP/commit/5481b2b8fd1ea1ff0659070a5608e1521d120193/))
* Correção da Linguagem Funcional 3 - ([commit](https://github.com/AugustoSampaio/PLP/commit/b523ebee335348ed12f03a4eecfd3234b703071d/))
* Mudança para utilizar ambiente auxiliar no elabora ao invés de map auxiliar - ([commit](https://github.com/AugustoSampaio/PLP/commit/43d900d6be77288786ad895c99f4bbc163e04244/))
* Atualizar a página da disciplina com as novas modificações
* Adicionar javacc sources para debug ([commit](https://github.com/fmca/PLP/commit/9606652f1f48717e75bc1a480c8ad8bff1bf5c3c))
* Corrigir problemas da LF3 ao executar quicksort ([commit](https://github.com/fmca/PLP/commit/31ea41c48fb3a7df3ac9e8ea4fbb980e08a66c78))
* Atualizar geração do applet ([commit](https://github.com/fmca/PLP/commit/9a9927f73eb57911ffbf46e3d60a0998e3842601))
* Incluir os módulos WebAPI e WebUI (nova IDE web) ([commit](https://github.com/AugustoSampaio/PLP/pull/25/commits/ca77ecd51e395adb066b3ce01b8471551463e9eb))
* Adicionar suporte à depuração nas linguagens, expondo o snapshot do ambiente de compilação ([commit](https://github.com/AugustoSampaio/PLP/pull/25/commits/a45521837356525c512ec353389f79b65602bf5a))

## Publicação

O GitHub Pages desta cópia é atualizado pelo workflow [docs.yml](.github/workflows/docs.yml) quando há alterações em `master`. A publicação compila os interpretadores, executa os testes e gera a documentação e a IDE. O resultado pode ser acompanhado na [aba Actions](https://github.com/CyberNicky/PLP/actions).

O repositório base e o site oficial da disciplina são mantidos pelo professor; esta publicação pertence à conta CyberNicky.

## Créditos

Baseado no projeto da equipe:

* Luana Martins (lms7@cin.ufpe.br)
* Marcel Rebouças (mscr@cin.ufpe.br)
* Renato Oliveira (ros3@cin.ufpe.br)

A nova IDE web (`WebUI`) foi desenvolvida por:

* Matheus Andrade (mvtna@cin.ufpe.br)
* Uanderson Silva (urfs@cin.ufpe.br)
