import sys
import netCDF4
from datetime import datetime, timedelta, date
#from os import rename
dset = netCDF4.Dataset(sys.argv[1], "r+","NETCDF3_64BIT_OFFSET")
print (dset.data_model)
print (dset.variables.keys())
#for t  in range(0,dset.variables['time'].size):
#  dset.variables['time'][t]+=365
indate="{0:-08d}".format(dset.variables['date'][0])
outdate=int(sys.argv[2]) #year
print (outdate)
print (indate)
d0 = date(int(indate[0:4]), 12, 1)
d1 = date(outdate-1, 12, 1)
delta = d1 - d0
print (delta.days)
siz= dset.variables['date'].size
for d in range(0,siz):
 s = "{0:-08d}".format(dset.variables['date'][d])
 date = datetime(year=int(s[0:4]), month=int(s[4:6]), day=int(s[6:8]))
 print (date)
 date += timedelta(days=delta.days)
 print (date)
 s = date.strftime("%Y%m%d")
 print (s)
 dset.variables['date'][d] = s

dset.close()
#rename ('h0001.2022', 'h0001.nc')
exit()

