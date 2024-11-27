import ollama
import openai
import os
import time
import sys 
import google.generativeai as genai
from constants import *
from utils import *
import jaro

openai.api_key = OPENAI_KEY
genai.configure(api_key=GEMINI_KEY)

def gerarRespostaChatGPT(contexto, instrucao):
    resposta = openai.ChatCompletion.create(
        model= CHATGPT_MODEL,
        messages = [
            {"role": "system", "content": contexto},
            {"role": "user", "content": instrucao},
        ],
        temperature = CHATGPT_TEMPERATURE,
    )
    return resposta['choices'][0].message.content.strip()

def gerarRespostaOllama(contexto, instrucao):
    client = ollama.Client()
    prompt = f'Context: {contexto}\n\nQuestion: {instrucao}'
    response = client.generate(model=LLAMA_MODEL, prompt=prompt)
    mensagem = response['response']
    return mensagem

def gerarRespostaGemini(contexto, instrucao):
    gemini_client = genai.GenerativeModel(model_name="gemini-1.5-flash", system_instruction=contexto)
    response = gemini_client.generate_content(instrucao)
    return response.text


def main(args):
    num_asserts_maximo = NUM_ASSERTS_MAXIMO

    rate_limit_per_minute = 10
    delay = 60 / rate_limit_per_minute

    for nome_arquivo in os.listdir(ASSERTS_FILES_DIR):
        print(nome_arquivo)
        asserts = ler_n_linhas_como_string(ASSERTS_FILES_DIR + nome_arquivo, num_asserts_maximo)
        conteudo_ferramenta = ler_n_linhas_como_string(REFACTORED_ASSERTS_DIR + nome_arquivo, num_asserts_maximo).splitlines()

        if not conteudo_ferramenta:
            continue    
        
        similaridade_chatgpt = 0 
        similaridade_gemini = 0
        n = 0
        for i, assert_original in enumerate(asserts.splitlines()):
            mensagem_ferramenta = ''
            try: 
                mensagem_ferramenta = conteudo_ferramenta[i].strip()
            except:
                continue 
            
            instrucao = INSTRUCAO + assert_original
            mensagem_chatgpt = gerarRespostaChatGPT(CONTEXTO, instrucao).strip()
            mensagem_gemini = gerarRespostaGemini(CONTEXTO, instrucao).strip()
            
            similaridade_chatgpt += jaro.jaro_metric(mensagem_ferramenta, mensagem_chatgpt)
            similaridade_gemini += jaro.jaro_metric(mensagem_ferramenta, mensagem_gemini)

            n += 1

            time.sleep(delay)

        similaridade_chatgpt /= n
        similaridade_gemini /= n

        saida = f'{nome_arquivo} & {similaridade_chatgpt:.2f} & {similaridade_gemini:.2f}\n'
        write_to_file(saida, ARQUIVO_TABELA_RESULTADOS, 'a')

        print(f'{nome_arquivo}; chatgpt: {similaridade_chatgpt:.2f}; gemini: {similaridade_gemini:.2f}')
        

if __name__ == '__main__':
    main(sys.argv[1:])

