using MedicaiFacility.BusinessObject;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MedicaiFacility.DataAccess.IRepostory
{
    public interface ISystemBalanceRepository
    {
        SystemBalance GetBalance(int id);
        void update(int SystemId, decimal amount);
    }
}
