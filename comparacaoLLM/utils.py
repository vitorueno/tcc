def write_to_file(content, file, operation='w'):
    f = open(file, operation)    
    f.write(content)
    f.close() 

def ler_n_linhas_como_string(arquivo, n):
    with open(arquivo, 'r') as f:
        return ''.join([linha for i, linha in enumerate(f) if i < int(n)])

