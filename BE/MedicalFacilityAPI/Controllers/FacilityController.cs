using MedicaiFacility.BusinessObject;
using MedicaiFacility.Service.IService;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace MedicalFacilityAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class FacilityController : ControllerBase
    {
        private readonly IMedicalFacilityService _facilityService;
        public FacilityController(IMedicalFacilityService facilityService)
        {
            _facilityService = facilityService; 
        }
        [HttpGet]
        public ActionResult<List<FacilityDepartment>> Get() {
            return Ok( _facilityService.GetAllMedicalFacility());
        }
    }
}
