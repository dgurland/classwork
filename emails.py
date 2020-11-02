'''
Created on Oct 20, 2020

@author: danar
'''
import csv
import string

def read_input_file(file_name):

    retrieved_values = {}

    with open(file_name) as csv_file:

        csv_reader = csv.reader(csv_file, delimiter=',')
        line_count = 0
        for row in csv_reader:
            if line_count < 3 or row[0] == '':
                line_count += 1
            else:
                vendor = row[0].lower()
                out = vendor.translate(str.maketrans('', '', string.punctuation))
                email = row[4]
                retrieved_values[out] = email
                line_count += 1
        print(f'Processed {line_count} lines.')
    return retrieved_values

def write_file(infile, outfile, data):
    with open(infile) as in_file:
        with open(outfile, mode='w', newline = '') as out_file:
            writer = csv.writer(out_file, delimiter=',', quotechar='"', quoting=csv.QUOTE_MINIMAL)
            csv_reader = csv.reader(in_file, delimiter=',')
            line_count = 0
            for row in csv_reader:
                if line_count < 1:
                    line_count += 1
                else:
                    vendor = row[1].lower()
                    out = vendor.translate(str.maketrans('', '', string.punctuation))
                    line_count += 1
                    if out in data:
                        row[2] = data[out]
                    else:
                        print("Did not find match for company " + out)
                writer.writerow(row)
        print(f'Wrote {line_count} lines. ')


emails = read_input_file('kettler_emails.csv')
write_file('mycoi_emails.csv', 'coiwithemails.csv', emails)