class Calculadora:
    def __init__(self):
        self.precedencia = {'+': 1, '-': 1, '*': 2, '/': 2, '^': 3}
    
    def calcular(self, expressao, notacao):
        """Calcula o resultado da expressao baseado na notacao informada"""
        if notacao == "infixa":
            pos_fixa = self.infixa_para_posfixa(expressao)
            return self.calcular_posfixa(pos_fixa), pos_fixa, self.posfixa_para_prefixa(pos_fixa)
        elif notacao == "pos-fixa":
            prefixa = self.posfixa_para_prefixa(expressao)
            return self.calcular_posfixa(expressao), self.posfixa_para_infixa(expressao), prefixa
        elif notacao == "pre-fixa":
            pos_fixa = self.prefixa_para_posfixa(expressao)
            return self.calcular_posfixa(pos_fixa), self.posfixa_para_infixa(pos_fixa), expressao
        else:
            raise ValueError("Notacao invalida")
    
    def infixa_para_posfixa(self, expressao):
        """Converte notacao infixa para pos-fixa"""
        pilha = []
        saida = []
        tokens = self._tokenizar(expressao)
        
        for token in tokens:
            if self._eh_numero(token):
                saida.append(token)
            elif token == '(':
                pilha.append(token)
            elif token == ')':
                while pilha and pilha[-1] != '(':
                    saida.append(pilha.pop())
                pilha.pop()  # Remove '('
            else:  # Operador
                while (pilha and pilha[-1] != '(' and 
                       self.precedencia.get(pilha[-1], 0) >= self.precedencia.get(token, 0)):
                    saida.append(pilha.pop())
                pilha.append(token)
        
        while pilha:
            saida.append(pilha.pop())
        
        return ' '.join(saida)
    
    def posfixa_para_infixa(self, expressao):
        """Converte notacao pos-fixa para infixa"""
        pilha = []
        tokens = expressao.split()
        
        for token in tokens:
            if self._eh_numero(token):
                pilha.append(token)
            else:
                dir = pilha.pop()
                esq = pilha.pop()
                # Adiciona parenteses apenas quando necessario
                expr = f"({esq} {token} {dir})"
                pilha.append(expr)
        
        return pilha[0][1:-1] if len(pilha) == 1 else pilha[0]  # Remove parenteses externos
    
    def posfixa_para_prefixa(self, expressao):
        """Converte notacao pos-fixa para pre-fixa"""
        pilha = []
        tokens = expressao.split()
        
        for token in tokens:
            if self._eh_numero(token):
                pilha.append(token)
            else:
                dir = pilha.pop()
                esq = pilha.pop()
                expr = f"{token} {esq} {dir}"
                pilha.append(expr)
        
        return pilha[0]
    
    def prefixa_para_posfixa(self, expressao):
        """Converte notacao pre-fixa para pos-fixa"""
        pilha = []
        tokens = expressao.split()[::-1]  # Inverte a ordem
        
        for token in tokens:
            if self._eh_numero(token):
                pilha.append(token)
            else:
                esq = pilha.pop()
                dir = pilha.pop()
                expr = f"{esq} {dir} {token}"
                pilha.append(expr)
        
        return pilha[0]
    
    def calcular_posfixa(self, expressao):
        """Calcula o resultado de uma expressao pos-fixa"""
        pilha = []
        tokens = expressao.split()
        
        for token in tokens:
            if self._eh_numero(token):
                pilha.append(float(token))
            else:
                dir = pilha.pop()
                esq = pilha.pop()
                resultado = self._aplicar_operador(esq, dir, token)
                pilha.append(resultado)
        
        return pilha[0]
    
    def _tokenizar(self, expressao):
        """Tokeniza uma expressao infixa"""
        tokens = []
        numero_atual = ""
        
        for char in expressao:
            if char == ' ':
                continue
            elif char in '()+-*/^':
                if numero_atual:
                    tokens.append(numero_atual)
                    numero_atual = ""
                tokens.append(char)
            else:  # Dígito ou ponto decimal
                numero_atual += char
        
        if numero_atual:
            tokens.append(numero_atual)
        
        return tokens
    
    def _eh_numero(self, token):
        """Verifica se o token e um numero"""
        try:
            float(token)
            return True
        except ValueError:
            return False
    
    def _aplicar_operador(self, a, b, operador):
        """Aplica operacao matematica basica"""
        if operador == '+':
            return a + b
        elif operador == '-':
            return a - b
        elif operador == '*':
            return a * b
        elif operador == '/':
            if b == 0:
                raise ValueError ("Divisao por zero")
            return a / b
        elif operador == '^':
            return a ** b
        else:
            raise ValueError(f"Operador invalido: {operador}")

def main():
    calculadora = Calculadora()
    
    print("=== CALCULADORA MULTI-NOTAcaO ===")
    print("Notacoes disponiveis:")
    print("1. Infixa (ex: (A + B) * C)")
    print("2. Pos-fixa (ex: A B + C *)")
    print("3. Pre-fixa (ex: * + A B C)")
    print()
    
    while True:
        print("\n" + "="*50)
        print("Escolha a notacao da expressao:")
        print("1 - Infixa")
        print("2 - Pos-fixa") 
        print("3 - Pre-fixa")
        print("0 - Sair")
        
        opcao = input("\nDigite sua opcao: ").strip()
        
        if opcao == '0':
            print("Saindo da calculadora...")
            break
        
        if opcao == '1':
            notacao = "infixa"
            exemplo = "(3 + 4) * 5"
        elif opcao == '2':
            notacao = "pos-fixa"
            exemplo = "3 4 + 5 *"
        elif opcao == '3':
            notacao = "pre-fixa"
            exemplo = "* + 3 4 5"
        else:
            print("Opcao invalida!")
            continue
        
        print(f"\nNotacao selecionada: {notacao}")
        print(f"Exemplo: {exemplo}")
        print("Operadores suportados: +, -, *, /, ^")
        
        expressao = input(f"\nDigite a expressao na notacao {notacao}: ").strip()
        
        if not expressao:
            print("Expressao vazia!")
            continue
        
        try:
            resultado, infixa, prefixa = calculadora.calcular(expressao, notacao)
            pos_fixa = calculadora.infixa_para_posfixa(infixa) if notacao != "pos-fixa" else expressao
            
            print("\n" + "="*50)
            print("RESULTADOS:")
            print(f"Resultado do calculo: {resultado}")
            print(f"Expressao Infixa: {infixa}")
            print(f"Expressao Pós-fixa: {pos_fixa}")
            print(f"Expressao Pré-fixa: {prefixa}")
            
        except Exception as e:
            print(f"\nErro: {e}")
            print("Verifique se a expressao esta correta!")

if __name__ == "__main__":
    main()