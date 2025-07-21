using MedicaiFacility.BusinessObject;
using MedicaiFacility.DataAccess.IRepostory;
using MedicaiFacility.Service.IService;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MedicaiFacility.Service
{
    public class SystembalanceService : ISystemBalanceService
    {
        private readonly ISystemBalanceRepository _systemBalanceRepository;
        public SystembalanceService(ISystemBalanceRepository systemBalanceRepository)
        {
            _systemBalanceRepository = systemBalanceRepository;
        }

        public SystemBalance GetBalance(int id)
        {
           return _systemBalanceRepository.GetBalance(id);
        }

        public void update(int SystemId, decimal amount)
        {
            _systemBalanceRepository.update(SystemId, amount);
        }
    }
}
