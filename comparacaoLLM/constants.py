from dotenv import load_dotenv
import os

# ler .env file 
load_dotenv()

OPENAI_KEY = os.getenv('OPENAI_KEY') # lido do .env
GEMINI_KEY = os.getenv('GEMINI_KEY')
CHATGPT_MODEL = 'gpt-3.5-turbo'
CHATGPT_TEMPERATURE = 0.7
LLAMA_MODEL = 'llama3.2'
GEMINI_MODEL = "gemini-1.5-flash"

CONTEXTO = 'você é um desenvolvedor senior especialista em testes unitários Junit com muitos anos ' +\
            'de experiência e recebe a missão de criar mensagens de descrição de erro de asserts, ' +\
            'o parâmetro opcional de asserts Junit, para corrigir o test smell de assertion roulette.\n' 

INSTRUCAO = '''
para cada assert abaixo gere uma mensagem apropriada que o descreva.
como resposta retorne apenas a mensagem em português conforme o exemplo: 

exemplo de entrada:
assertEquals(5, calculator.add(2, 3));
assertEquals(7, calculator.add(4, 2));
assertEquals(10, calculator.add(5, 5));

exemplo de saída:
eram esperados valores iguais, mas 5 é diferente de calculator.add(2, 3)
eram esperados valores iguais, mas 7 é diferente de calculator.add(4, 2)
eram esperados valores iguais, mas 10 é diferente de calculator.add(5, 5)

entradas reais:\n 
'''

INSTRUCAO_ANTIGO = 'para cada assert abaixo, gere o parâmetro opcional de mensagem de descrição de erro de asserts Junit. ' +\
'Retorne apenas a mensagem gerada em português, sem estar dentro do método de assert. ' +\
'Também não dê explicações, exemplos, contexto ou códigos, apenas o parâmetro gerado \n' 

ASSERTS_FILES_DIR = '../experimento_llm/'
REFACTORED_ASSERTS_DIR = '../experimento_refactor/'

NUM_ASSERTS_MAXIMO = 100

SUFIXO_CHATGPT = '_chatgpt.log'
SUFIXO_GEMINI = '_gemini.log'

ARQUIVO_TABELA_RESULTADOS = 'resultados.txt'
ARQUIVO_TABELA_MEDIAS = 'medias.txt'