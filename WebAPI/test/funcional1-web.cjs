// Exercita o JavaScript gerado pelo TeaVM e a mesma API chamada pela IDE.
const fs = require('node:fs');
const path = require('node:path');
const vm = require('node:vm');
const assert = require('node:assert/strict');
const root = path.resolve(__dirname, '../..');
const context = vm.createContext({ console });
vm.runInContext(fs.readFileSync(path.join(root, 'WebAPI/target/javascript/plp.js'), 'utf8'), context);
let cases = 0;
for (const line of fs.readFileSync(path.join(root, 'Funcional1/examples/resultados.tsv'), 'utf8').split(/\r?\n/)) {
  if (!line || line.startsWith('#')) continue;
  const [file, error, expected] = line.split('\t');
  const source = fs.readFileSync(path.join(root, 'Funcional1/examples', file), 'utf8');
  const result = context.__runCode('func1', source, '');
  assert.equal(result.success, error === '0', `${file}: ${JSON.stringify(result)}`);
  if (result.success) assert.equal(result.output, expected, file);
  else assert.ok(result.message?.includes(expected), `${file}: ${result.message}`);
  cases++;
}
// Uma chamada inválida não contamina as execuções seguintes.
assert.equal(context.__runCode('func1', 'head([4,5])', '').output, '4');
assert.equal(context.__runCode('exp1', '1 + 2', '').output, '3');
console.log(`${cases} exemplos de Funcional1 passaram na API JavaScript da IDE; reexecução e Exp1 também passaram.`);
