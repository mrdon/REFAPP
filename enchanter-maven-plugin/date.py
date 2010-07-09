ssh.connect('${server}', '${user}')
ssh.waitFor('${unix_prompt}');
ssh.sendLine('date');
print 'Enchanter:server date is '+ssh.getLine();
ssh.disconnect();