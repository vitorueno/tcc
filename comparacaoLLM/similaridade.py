import jaro
import os
from constants import *
from utils import *


run1_dir = './mensagem1/tentativa10/' 
run2_dir = './mensagem1/tentativa11/' 

def main():
    soma_chatgpt_run1 = 0
    soma_chatgpt_run2 = 0
    soma_chatgpt_media = 0
    soma_gemini_run1 = 0
    soma_gemini_run2 = 0
    soma_gemini_media = 0

    qntd_projetos_considerados = 0
    for nome_arquivo in os.listdir(REFACTORED_ASSERTS_DIR):
        conteudo_ferramenta = ler_n_linhas_como_string(REFACTORED_ASSERTS_DIR + nome_arquivo, NUM_ASSERTS_MAXIMO) 
        conteudo_chatgpt_run1 = ler_n_linhas_como_string(run1_dir + nome_arquivo + SUFIXO_CHATGPT, NUM_ASSERTS_MAXIMO)
        conteudo_chatgpt_run2 = ler_n_linhas_como_string(run2_dir + nome_arquivo + SUFIXO_CHATGPT, NUM_ASSERTS_MAXIMO)
        conteudo_gemini_run1 = ler_n_linhas_como_string(run1_dir + nome_arquivo + SUFIXO_GEMINI, NUM_ASSERTS_MAXIMO)
        conteudo_gemini_run2 = ler_n_linhas_como_string(run2_dir + nome_arquivo + SUFIXO_GEMINI, NUM_ASSERTS_MAXIMO)
        
        # comparando arquivo como um todo
        # similaridade_chatgpt_1 = jaro.jaro_metric(conteudo_ferramenta, conteudo_chatgpt_run1)
        # similaridade_chatgpt_2 = jaro.jaro_metric(conteudo_ferramenta, conteudo_chatgpt_run2)
        # similaridade_chatgpt_media = (similaridade_chatgpt_1 + similaridade_chatgpt_2) / 2
        # similaridade_gemini_1 = jaro.jaro_metric(conteudo_ferramenta, conteudo_gemini_run1)
        # similaridade_gemini_2 = jaro.jaro_metric(conteudo_ferramenta, conteudo_gemini_run2)
        # similaridade_gemini_media = (similaridade_gemini_1 + similaridade_gemini_2) / 2

        similaridade_chatgpt_1 = 0 
        similaridade_chatgpt_2 = 0 
        similaridade_chatgpt_media = 0
        similaridade_gemini_1 = 0 
        similaridade_gemini_2 = 0
        similaridade_gemini_media = 0
        n = 0

        for i, linha in enumerate(conteudo_ferramenta.splitlines()):
            n += 1
            similaridade_chatgpt_1 += get_similaridade(linha, conteudo_chatgpt_run1, i) 
            similaridade_chatgpt_2 += get_similaridade(linha, conteudo_chatgpt_run2, i)
            similaridade_gemini_1 += get_similaridade(linha, conteudo_gemini_run1, i) 
            similaridade_gemini_2 += get_similaridade(linha, conteudo_gemini_run2, i)
            similaridade_chatgpt_media += (similaridade_chatgpt_1 + similaridade_chatgpt_2) / 2
            similaridade_gemini_media += (similaridade_gemini_1 + similaridade_gemini_2) / 2
            
        if n == 0:
            continue

        similaridade_chatgpt_1 /= n 
        similaridade_chatgpt_2 /= n 
        similaridade_chatgpt_media /= n
        similaridade_gemini_1 /= n 
        similaridade_gemini_2 /= n
        similaridade_gemini_media /= n

        # if (not similaridade_chatgpt_1 or not similaridade_chatgpt_2 or not similaridade_gemini_1 or not similaridade_gemini_2):
        #     continue

        qntd_projetos_considerados += 1
        soma_chatgpt_run1 += similaridade_chatgpt_1
        soma_chatgpt_run2 += similaridade_chatgpt_2
        soma_chatgpt_media += similaridade_chatgpt_media
        soma_gemini_run1 += similaridade_gemini_1
        soma_gemini_run2 += similaridade_gemini_2
        soma_gemini_media += similaridade_gemini_media

        saida_duas_runs = f'{nome_arquivo} & {similaridade_chatgpt_1:.2f} & {similaridade_chatgpt_2:.2f} & {similaridade_gemini_1:.2f} & {similaridade_gemini_2:.2f}\n'
        saida_medias = f'{nome_arquivo} & {similaridade_chatgpt_media:.2f} & {similaridade_gemini_media:.2f}\n'

        write_to_file(saida_duas_runs, ARQUIVO_TABELA_RESULTADOS, 'a')
        write_to_file(saida_medias, ARQUIVO_TABELA_MEDIAS, 'a')

    media_soma_chatgpt_run1 = soma_chatgpt_run1 / qntd_projetos_considerados
    media_soma_chatgpt_run2 = soma_chatgpt_run2 / qntd_projetos_considerados
    media_media_chatgpt = soma_chatgpt_media / qntd_projetos_considerados

    media_soma_gemini_run1 = soma_gemini_run1 / qntd_projetos_considerados
    media_soma_gemini_run2 = soma_gemini_run2 / qntd_projetos_considerados
    media_media_gemini = soma_gemini_media / qntd_projetos_considerados 

    linha_final_resultados = f'media & {media_soma_chatgpt_run1:.2f} & {media_soma_chatgpt_run2:.2f} & {media_soma_gemini_run1:.2f} & {media_soma_gemini_run2:.2f}\n' 
    linha_final_media = f'media & {media_media_chatgpt:.2f} & {media_media_gemini:.2f}\n' 
    
    write_to_file(linha_final_resultados, ARQUIVO_TABELA_RESULTADOS, 'a')
    write_to_file(linha_final_media, ARQUIVO_TABELA_MEDIAS, 'a')
       
def get_similaridade(conteudo, linhas_llm, i):
    similaridade = 0
    try: 
        similaridade = jaro.jaro_metric(conteudo, linhas_llm.splitlines()[i]) 
    except Exception as e: 
        pass

    return similaridade   

if __name__ == '__main__':
    main()